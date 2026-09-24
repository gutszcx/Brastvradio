package com.example.ui.player

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.model.StreamProtocol
import com.example.util.StreamUrlHelper
import com.example.data.model.TvChannel
import com.example.ui.theme.BrasilGreen
import com.example.ui.theme.BrasilYellow
import com.example.ui.theme.CastPurple
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PrimeBlue
import com.example.ui.viewmodel.VideoAspect
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    channel: TvChannel?,
    isPlaying: Boolean,
    isMuted: Boolean,
    videoAspect: VideoAspect,
    isFullscreen: Boolean,
    isCasting: Boolean,
    sleepTimerRemainingSeconds: Int,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onCycleAspect: () -> Unit,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    onOpenCast: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showControls by remember { mutableStateOf(true) }
    val context = LocalContext.current

    var isWebPlayerMode by remember(channel?.id) {
        mutableStateOf(
            channel?.protocol == StreamProtocol.YOUTUBE_LIVE ||
            channel?.protocol == StreamProtocol.WEB_EMBED ||
            !StreamUrlHelper.isDirectMediaStream(channel?.streamUrl ?: "")
        )
    }

    // Auto-hide controls after 4 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isFullscreen) Modifier.fillMaxSize()
                else Modifier.aspectRatio(16f / 9f)
            )
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("video_player_container")
    ) {
        if (channel != null) {
            if (isWebPlayerMode) {
                WebEmbedPlayerContainer(
                    channel = channel,
                    isPlaying = isPlaying,
                    isMuted = isMuted
                )
            } else {
                ExoPlayerContainer(
                    channel = channel,
                    isPlaying = isPlaying,
                    isMuted = isMuted,
                    videoAspect = videoAspect,
                    onNextChannel = onNextChannel,
                    onSwitchToWebPlayer = { isWebPlayerMode = true }
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Selecione um canal para assistir",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = showControls || !isPlaying,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerOverlayControls(
                channel = channel,
                isPlaying = isPlaying,
                isMuted = isMuted,
                videoAspect = videoAspect,
                isFullscreen = isFullscreen,
                isCasting = isCasting,
                isWebPlayerMode = isWebPlayerMode,
                sleepTimerRemainingSeconds = sleepTimerRemainingSeconds,
                onTogglePlayPause = onTogglePlayPause,
                onToggleMute = onToggleMute,
                onToggleFullscreen = onToggleFullscreen,
                onTogglePlayerEngine = { isWebPlayerMode = !isWebPlayerMode },
                onCycleAspect = onCycleAspect,
                onNextChannel = onNextChannel,
                onPrevChannel = onPrevChannel,
                onOpenCast = onOpenCast,
                onOpenSleepTimer = onOpenSleepTimer
            )
        }
    }
}

private fun isNetworkConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
    val activeNetwork = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@OptIn(UnstableApi::class)
@Composable
private fun ExoPlayerContainer(
    channel: TvChannel,
    isPlaying: Boolean,
    isMuted: Boolean,
    videoAspect: VideoAspect,
    onNextChannel: () -> Unit,
    onSwitchToWebPlayer: () -> Unit
) {
    val context = LocalContext.current
    var isBuffering by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val httpDataSourceFactory = remember {
        DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(10000)
            .setReadTimeoutMs(15000)
            .setKeepPostFor302Redirects(true)
            .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36 BrasilTV/2.0")
            .setDefaultRequestProperties(
                mapOf(
                    "Accept" to "*/*",
                    "Connection" to "keep-alive"
                )
            )
    }

    val mediaSourceFactory = remember(context, httpDataSourceFactory) {
        DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                playWhenReady = isPlaying
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            try {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.release()
            } catch (_: Throwable) {}
        }
    }

    DisposableEffect(channel.id, channel.streamUrl, exoPlayer) {
        hasError = false
        isBuffering = true
        errorMessage = null

        val isOnline = isNetworkConnected(context)
        if (!isOnline) {
            hasError = true
            isBuffering = false
            errorMessage = "Sem conexão com a internet. Verifique seu Wi-Fi ou dados móveis."
        } else {
            try {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                val mimeType = StreamUrlHelper.getMimeTypeForExoPlayer(channel.streamUrl)
                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.parse(channel.streamUrl.trim()))
                    .apply {
                        if (mimeType != null) {
                            setMimeType(mimeType)
                        }
                    }
                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = isPlaying
            } catch (_: Throwable) {
                hasError = true
                isBuffering = false
                errorMessage = "Não foi possível carregar a transmissão."
            }
        }

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    hasError = false
                    errorMessage = null
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    try {
                        exoPlayer.seekToDefaultPosition()
                        exoPlayer.prepare()
                        return
                    } catch (_: Throwable) {}
                }

                hasError = true
                isBuffering = false
                errorMessage = when {
                    !isNetworkConnected(context) -> "Sem conexão com a internet."
                    error.cause is java.net.UnknownHostException -> "Sinal temporariamente fora do ar."
                    error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Falha na conexão com a emissora."
                    error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Tempo de resposta esgotado."
                    else -> "Transmissão temporariamente indisponível no player nativo."
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            try {
                exoPlayer.removeListener(listener)
            } catch (_: Throwable) {}
        }
    }

    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    val resizeMode = when (videoAspect) {
        VideoAspect.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        VideoAspect.FILL_16_9 -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        VideoAspect.ZOOM_STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    this.resizeMode = resizeMode
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.resizeMode = resizeMode
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isBuffering && !hasError) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimeBlue, modifier = Modifier.size(38.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Carregando sinal ao vivo...", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD0A0A0A)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = "Sinal indisponível",
                                tint = BrasilYellow,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = errorMessage ?: "Sinal ao vivo temporariamente instável",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = "Toque para reconectar, abra no Modo Web ou mude de canal",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                hasError = false
                                isBuffering = true
                                errorMessage = null
                                try {
                                    exoPlayer.stop()
                                    exoPlayer.clearMediaItems()
                                    val mimeType = StreamUrlHelper.getMimeTypeForExoPlayer(channel.streamUrl)
                                    val mediaItem = MediaItem.Builder()
                                        .setUri(Uri.parse(channel.streamUrl.trim()))
                                        .apply {
                                            if (mimeType != null) {
                                                setMimeType(mimeType)
                                            }
                                        }
                                        .build()
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.prepare()
                                    exoPlayer.play()
                                } catch (_: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimeBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Reconectar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = onSwitchToWebPlayer,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Modo Web", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onNextChannel,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                Icons.Default.FastForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Próximo", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebEmbedPlayerContainer(
    channel: TvChannel,
    isPlaying: Boolean,
    isMuted: Boolean
) {
    val htmlData = remember(channel.streamUrl, channel.id, isMuted) {
        StreamUrlHelper.generateWebPlayerHtml(channel.streamUrl, isMuted)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.databaseEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                settings.userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36 BrasilTV/2.0"
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                loadDataWithBaseURL("https://localhost", htmlData, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            // Reload on channel change if necessary
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun PlayerOverlayControls(
    channel: TvChannel?,
    isPlaying: Boolean,
    isMuted: Boolean,
    videoAspect: VideoAspect,
    isFullscreen: Boolean,
    isCasting: Boolean,
    isWebPlayerMode: Boolean,
    sleepTimerRemainingSeconds: Int,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onTogglePlayerEngine: () -> Unit,
    onCycleAspect: () -> Unit,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    onOpenCast: () -> Unit,
    onOpenSleepTimer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.75f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.85f)
                    )
                )
            )
            .padding(12.dp)
    ) {
        // Top Bar: Channel Info & Badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Live red dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(LiveRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AO VIVO",
                    color = LiveRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))

                if (channel != null) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = channel.number,
                            color = BrasilYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Top action buttons: Player Engine Toggle, Sleep Timer & Cast
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Player Engine Badge / Switcher
                Surface(
                    color = if (isWebPlayerMode) Color(0xFF1976D2).copy(alpha = 0.35f) else PrimeBlue.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onTogglePlayerEngine() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isWebPlayerMode) Icons.Default.Language else Icons.Default.PlayCircle,
                            contentDescription = "Alternar Modo Player",
                            tint = if (isWebPlayerMode) Color(0xFF90CAF9) else PrimeBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isWebPlayerMode) "WEB" else "NATIVO",
                            color = if (isWebPlayerMode) Color(0xFF90CAF9) else PrimeBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (sleepTimerRemainingSeconds > 0) {
                    val min = sleepTimerRemainingSeconds / 60
                    Surface(
                        color = BrasilYellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { onOpenSleepTimer() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Temporizador",
                                tint = BrasilYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${min}m",
                                color = BrasilYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    IconButton(
                        onClick = onOpenSleepTimer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Temporizador de Desligamento",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onOpenCast,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("player_cast_button")
                ) {
                    Icon(
                        imageVector = if (isCasting) Icons.Default.CastConnected else Icons.Default.Cast,
                        contentDescription = "Transmitir para TV / Chromecast",
                        tint = if (isCasting) CastPurple else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Center Zapping Controls
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevChannel,
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("prev_channel_button")
            ) {
                Icon(
                    Icons.Default.FastRewind,
                    contentDescription = "Canal Anterior",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier
                    .size(62.dp)
                    .background(PrimeBlue, CircleShape)
                    .testTag("play_pause_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = onNextChannel,
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("next_channel_button")
            ) {
                Icon(
                    Icons.Default.FastForward,
                    contentDescription = "Próximo Canal",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Bottom Controls: Current Program Info & Quick Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (channel != null) {
                    Text(
                        text = channel.currentProgram,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${channel.state} • ${channel.resolution}",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = if (isMuted) "Ativar som" else "Mudo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onCycleAspect,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.AspectRatio,
                        contentDescription = "Proporção de Tela",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onToggleFullscreen,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("fullscreen_button")
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isFullscreen) "Sair de tela cheia" else "Tela cheia",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
