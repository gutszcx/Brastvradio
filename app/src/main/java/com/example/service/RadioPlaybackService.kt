package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.MainActivity
import com.example.R
import com.example.data.datasource.RadioStationsData
import com.example.data.model.RadioStation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Foreground Audio Service supporting lock-screen playback, notification media controls,
 * wake lock, audio focus and seamless background streaming.
 */
class RadioPlaybackService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var hasTriedBackup = false

    companion object {
        const val CHANNEL_ID = "brasil_radio_playback_channel"
        const val NOTIFICATION_ID = 1002

        const val ACTION_PLAY_RADIO = "com.example.action.PLAY_RADIO"
        const val ACTION_TOGGLE_PLAY = "com.example.action.TOGGLE_PLAY"
        const val ACTION_STOP = "com.example.action.STOP"
        const val ACTION_NEXT = "com.example.action.NEXT"
        const val ACTION_PREV = "com.example.action.PREV"
        const val EXTRA_RADIO_ID = "extra_radio_id"

        // Observable State for UI
        private val _currentRadio = MutableStateFlow<RadioStation?>(null)
        val currentRadio: StateFlow<RadioStation?> = _currentRadio.asStateFlow()

        private val _isPlaying = MutableStateFlow(false)
        val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

        private val _isBuffering = MutableStateFlow(false)
        val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        private val _radioVolume = MutableStateFlow(1.0f)
        val radioVolume: StateFlow<Float> = _radioVolume.asStateFlow()

        fun playRadio(context: Context, station: RadioStation) {
            val intent = Intent(context, RadioPlaybackService::class.java).apply {
                action = ACTION_PLAY_RADIO
                putExtra(EXTRA_RADIO_ID, station.id)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun togglePlayPause(context: Context) {
            val intent = Intent(context, RadioPlaybackService::class.java).apply {
                action = ACTION_TOGGLE_PLAY
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopRadio(context: Context) {
            val intent = Intent(context, RadioPlaybackService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun setVolume(volume: Float) {
            _radioVolume.value = volume.coerceIn(0f, 1f)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(20000)

        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(httpDataSourceFactory)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true) // Handles Audio Focus automatically
            .setHandleAudioBecomingNoisy(true) // Pauses when headphones unplugged
            .setWakeMode(C.WAKE_MODE_NETWORK) // Keeps WiFi/Network and CPU active when screen locked
            .build().apply {
                volume = _radioVolume.value
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        _isPlaying.value = playing
                        updateForegroundNotification()
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                _isBuffering.value = true
                                _errorMessage.value = null
                            }
                            Player.STATE_READY -> {
                                _isBuffering.value = false
                                _errorMessage.value = null
                                hasTriedBackup = false
                            }
                            Player.STATE_ENDED -> {
                                _isBuffering.value = false
                            }
                            Player.STATE_IDLE -> {
                                _isBuffering.value = false
                            }
                        }
                        updateForegroundNotification()
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _isBuffering.value = false
                        _isPlaying.value = false
                        val current = _currentRadio.value
                        val backup = current?.backupStreamUrl
                        if (current != null && !hasTriedBackup && !backup.isNullOrBlank() && backup != current.streamUrl) {
                            hasTriedBackup = true
                            _errorMessage.value = "Tentando sinal alternativo..."
                            playBackupStation(current)
                        } else {
                            hasTriedBackup = false
                            _errorMessage.value = "Emissora temporariamente fora do ar."
                        }
                        updateForegroundNotification()
                    }
                })
            }

        mediaSession = MediaSession.Builder(this, player!!)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_RADIO -> {
                val radioId = intent.getStringExtra(EXTRA_RADIO_ID)
                val station = RadioStationsData.sampleRadios.firstOrNull { it.id == radioId }
                    ?: _currentRadio.value
                if (station != null) {
                    _currentRadio.value = station
                    updateForegroundNotification()
                    playStation(station)
                }
            }
            ACTION_TOGGLE_PLAY -> {
                val exo = player
                if (exo != null) {
                    if (exo.isPlaying) {
                        exo.pause()
                    } else {
                        if (exo.playbackState == Player.STATE_IDLE && _currentRadio.value != null) {
                            playStation(_currentRadio.value!!)
                        } else {
                            exo.play()
                        }
                    }
                }
            }
            ACTION_STOP -> {
                player?.stop()
                _isPlaying.value = false
                _isBuffering.value = false
                _currentRadio.value = null
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_NEXT -> {
                playNextStation(forward = true)
            }
            ACTION_PREV -> {
                playNextStation(forward = false)
            }
        }

        updateForegroundNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun playStation(station: RadioStation) {
        val exo = player ?: return
        hasTriedBackup = false
        try {
            exo.volume = _radioVolume.value
            val metadata = MediaMetadata.Builder()
                .setTitle(station.name)
                .setArtist("${station.dial} • ${station.cityState}")
                .setDisplayTitle(station.name)
                .setSubtitle(station.currentShow)
                .build()

            val mimeType = when {
                station.streamUrl.contains(".m3u8", ignoreCase = true) -> MimeTypes.APPLICATION_M3U8
                station.streamUrl.contains(".mpd", ignoreCase = true) -> MimeTypes.APPLICATION_MPD
                station.streamUrl.contains(".aac", ignoreCase = true) -> MimeTypes.AUDIO_AAC
                station.streamUrl.contains(".mp3", ignoreCase = true) -> MimeTypes.AUDIO_MPEG
                else -> null
            }

            val mediaItem = MediaItem.Builder()
                .setUri(station.streamUrl.trim())
                .apply {
                    if (mimeType != null) {
                        setMimeType(mimeType)
                    }
                }
                .setMediaMetadata(metadata)
                .build()

            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.play()
        } catch (e: Throwable) {
            _errorMessage.value = "Falha ao iniciar rádio: ${e.localizedMessage}"
        }
    }

    private fun playBackupStation(station: RadioStation) {
        val exo = player ?: return
        val backupUrl = station.backupStreamUrl?.trim()
        if (backupUrl.isNullOrEmpty()) {
            _errorMessage.value = "Emissora temporariamente indisponível."
            return
        }
        try {
            val mimeType = when {
                backupUrl.contains(".m3u8", ignoreCase = true) -> MimeTypes.APPLICATION_M3U8
                backupUrl.contains(".mpd", ignoreCase = true) -> MimeTypes.APPLICATION_MPD
                backupUrl.contains(".aac", ignoreCase = true) -> MimeTypes.AUDIO_AAC
                backupUrl.contains(".mp3", ignoreCase = true) -> MimeTypes.AUDIO_MPEG
                else -> null
            }
            val metadata = MediaMetadata.Builder()
                .setTitle(station.name)
                .setArtist("${station.dial} • Sinal Secundário")
                .setDisplayTitle(station.name)
                .setSubtitle(station.currentShow)
                .build()
            val mediaItem = MediaItem.Builder()
                .setUri(backupUrl)
                .apply {
                    if (mimeType != null) {
                        setMimeType(mimeType)
                    }
                }
                .setMediaMetadata(metadata)
                .build()
            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.play()
        } catch (_: Throwable) {
            _errorMessage.value = "Emissora temporariamente indisponível."
        }
    }

    private fun playNextStation(forward: Boolean) {
        val all = RadioStationsData.sampleRadios
        val current = _currentRadio.value ?: return
        val currentIndex = all.indexOfFirst { it.id == current.id }
        if (currentIndex != -1) {
            val nextIndex = if (forward) {
                (currentIndex + 1) % all.size
            } else {
                if (currentIndex - 1 < 0) all.size - 1 else currentIndex - 1
            }
            val nextStation = all[nextIndex]
            _currentRadio.value = nextStation
            playStation(nextStation)
        }
    }

    private fun updateForegroundNotification() {
        val station = _currentRadio.value
        if (station == null) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            return
        }

        val openAppIntent = PendingIntent.getActivity(
            this,
            1,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val toggleIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, RadioPlaybackService::class.java).apply { action = ACTION_TOGGLE_PLAY },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, RadioPlaybackService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = PendingIntent.getService(
            this,
            4,
            Intent(this, RadioPlaybackService::class.java).apply { action = ACTION_NEXT },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isCurrentlyPlaying = player?.isPlaying == true

        // Generate synthetic station badge icon
        val largeBadge = createBadgeBitmap(station.logoText, station.accentColorHex)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeBadge)
            .setContentTitle(station.name)
            .setContentText("${station.dial} • ${station.cityState}")
            .setSubText(if (_isBuffering.value) "Sintonizando ao vivo..." else station.genre.displayName)
            .setContentIntent(openAppIntent)
            .setOngoing(isCurrentlyPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                if (isCurrentlyPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isCurrentlyPlaying) "Pausar" else "Reproduzir",
                toggleIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "Próxima",
                nextIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Parar",
                stopIntent
            )
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession!!)
                    .setShowActionsInCompactView(0, 1)
            )

        val notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createBadgeBitmap(text: String, colorHex: Long): Bitmap {
        val size = 128
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorHex.toInt()
        }
        canvas.drawRoundRect(RectF(0f, 0f, size.toFloat(), size.toFloat()), 24f, 24f, bgPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        canvas.drawText(text.take(6), (size / 2).toFloat(), yPos, textPaint)
        return bitmap
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rádio Online Brasil (Ao Vivo)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Reprodução de rádio brasileira em segundo plano e tela bloqueada"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        player = null
        super.onDestroy()
    }
}
