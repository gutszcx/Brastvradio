package com.example.util

import android.net.Uri
import androidx.media3.common.MimeTypes
import com.example.data.model.StreamProtocol
import java.util.regex.Pattern

object StreamUrlHelper {

    /**
     * Determines whether a given URL is a YouTube link or video/channel ID.
     */
    fun isYouTubeUrl(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.contains("youtube.com") ||
                lower.contains("youtu.be") ||
                lower.startsWith("live_stream_") ||
                (lower.length == 11 && !lower.contains("/") && !lower.contains("."))
    }

    /**
     * Determines whether a given URL is a Twitch live channel or stream.
     */
    fun isTwitchUrl(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.contains("twitch.tv")
    }

    /**
     * Determines whether a given URL is a Dailymotion link.
     */
    fun isDailymotionUrl(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.contains("dailymotion.com") || lower.contains("dai.ly")
    }

    /**
     * Determines whether a given URL is a DASH stream (.mpd).
     */
    fun isDashUrl(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.contains(".mpd") || lower.contains("/dash/")
    }

    /**
     * Determines whether a given URL is an HLS stream (.m3u8, .m3u).
     */
    fun isHlsUrl(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.contains(".m3u8") ||
                lower.contains(".m3u") ||
                lower.contains("/hls/") ||
                lower.contains("format=m3u8") ||
                lower.contains("type=m3u8")
    }

    /**
     * Determines whether a URL is a direct playable media stream (HLS, DASH, MP4, etc.)
     * for ExoPlayer.
     */
    fun isDirectMediaStream(url: String): Boolean {
        val lower = url.trim().lowercase()
        return isHlsUrl(lower) ||
                isDashUrl(lower) ||
                lower.contains(".mp4") ||
                lower.contains(".ts") ||
                lower.contains(".mkv") ||
                lower.contains(".webm") ||
                lower.contains("manifest")
    }

    /**
     * Automatically detects the appropriate StreamProtocol from the URL.
     */
    fun detectProtocol(url: String, explicitIsYoutube: Boolean = false): StreamProtocol {
        if (explicitIsYoutube || isYouTubeUrl(url)) {
            return StreamProtocol.YOUTUBE_LIVE
        }
        if (isDashUrl(url)) {
            return StreamProtocol.DASH_MPD
        }
        if (isHlsUrl(url) || url.contains(".ts") || url.contains(".mp4")) {
            return StreamProtocol.HLS_M3U8
        }
        if (isTwitchUrl(url) || isDailymotionUrl(url) || url.contains("embed") || url.contains("player") || url.contains(".html") || !isDirectMediaStream(url)) {
            return StreamProtocol.WEB_EMBED
        }
        return StreamProtocol.HLS_M3U8
    }

    /**
     * Maps URLs to the appropriate Media3 MimeTypes.
     */
    fun getMimeTypeForExoPlayer(url: String): String? {
        val lower = url.trim().lowercase()
        return when {
            lower.contains(".m3u8") || lower.contains("/hls/") || lower.contains(".m3u") -> MimeTypes.APPLICATION_M3U8
            lower.contains(".mpd") || lower.contains("/dash/") -> MimeTypes.APPLICATION_MPD
            lower.contains(".ism") -> MimeTypes.APPLICATION_SS
            lower.contains(".mp4") -> MimeTypes.VIDEO_MP4
            lower.contains(".webm") -> MimeTypes.VIDEO_WEBM
            lower.contains(".ts") -> MimeTypes.VIDEO_MP2T
            else -> null
        }
    }

    /**
     * Extracts YouTube video ID or channel live stream embed URL.
     */
    fun getYouTubeEmbedUrl(url: String, isMuted: Boolean = false): String {
        val trimmed = url.trim()
        val muteParam = if (isMuted) 1 else 0

        // If already an embed URL
        if (trimmed.contains("youtube.com/embed/")) {
            val delimiter = if (trimmed.contains("?")) "&" else "?"
            return "$trimmed${delimiter}autoplay=1&mute=$muteParam&playsinline=1&controls=1&modestbranding=1"
        }

        // Live stream channel ID: https://www.youtube.com/channel/CHANNEL_ID/live or live_stream?channel=...
        val channelMatch = Pattern.compile("(?:channel/|channel=)([a-zA-Z0-9_-]{20,})").matcher(trimmed)
        if (channelMatch.find()) {
            val channelId = channelMatch.group(1)
            return "https://www.youtube.com/embed/live_stream?channel=$channelId&autoplay=1&mute=$muteParam&playsinline=1&controls=1&modestbranding=1"
        }

        // Direct video ID patterns (watch?v=ID, youtu.be/ID, live/ID, embed/ID)
        val videoPattern = Pattern.compile("(?:v=|/v/|youtu\\.be/|/embed/|/live/)([a-zA-Z0-9_-]{11})")
        val videoMatcher = videoPattern.matcher(trimmed)
        if (videoMatcher.find()) {
            val videoId = videoMatcher.group(1)
            return "https://www.youtube.com/embed/$videoId?autoplay=1&mute=$muteParam&playsinline=1&controls=1&modestbranding=1"
        }

        // If 11 character ID provided directly
        if (trimmed.length == 11 && !trimmed.contains("/") && !trimmed.contains(".")) {
            return "https://www.youtube.com/embed/$trimmed?autoplay=1&mute=$muteParam&playsinline=1&controls=1&modestbranding=1"
        }

        return "https://www.youtube.com/embed/$trimmed?autoplay=1&mute=$muteParam&playsinline=1&controls=1"
    }

    /**
     * Formats Twitch streams into a valid player embed.
     */
    fun getTwitchEmbedUrl(url: String, isMuted: Boolean = false): String {
        val trimmed = url.trim()
        val channel = trimmed.substringAfter("twitch.tv/").substringBefore("/").substringBefore("?")
        val muteParam = if (isMuted) "true" else "false"
        return "https://player.twitch.tv/?channel=$channel&parent=localhost&autoplay=true&muted=$muteParam"
    }

    /**
     * Formats Dailymotion streams into a valid player embed.
     */
    fun getDailymotionEmbedUrl(url: String, isMuted: Boolean = false): String {
        val trimmed = url.trim()
        val muteParam = if (isMuted) 1 else 0
        val id = if (trimmed.contains("dai.ly/")) {
            trimmed.substringAfter("dai.ly/").substringBefore("?")
        } else {
            trimmed.substringAfter("video/").substringBefore("?").substringBefore("_")
        }
        return "https://www.dailymotion.com/embed/video/$id?autoplay=1&mute=$muteParam"
    }

    /**
     * Builds a self-contained, responsive HTML5 player with Hls.js fallback
     * or iframe embed for WebView playback.
     */
    fun generateWebPlayerHtml(streamUrl: String, isMuted: Boolean = false): String {
        val trimmed = streamUrl.trim()
        val isMuteString = if (isMuted) "muted" else ""
        val muteBool = if (isMuted) "true" else "false"

        // YouTube Embed
        if (isYouTubeUrl(trimmed)) {
            val embedUrl = getYouTubeEmbedUrl(trimmed, isMuted)
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                        iframe { width: 100%; height: 100%; border: 0; display: block; }
                    </style>
                </head>
                <body>
                    <iframe 
                        src="$embedUrl" 
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                        allowfullscreen>
                    </iframe>
                </body>
                </html>
            """.trimIndent()
        }

        // Twitch Embed
        if (isTwitchUrl(trimmed)) {
            val embedUrl = getTwitchEmbedUrl(trimmed, isMuted)
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                        iframe { width: 100%; height: 100%; border: 0; }
                    </style>
                </head>
                <body>
                    <iframe src="$embedUrl" allowfullscreen allow="autoplay; fullscreen"></iframe>
                </body>
                </html>
            """.trimIndent()
        }

        // Dailymotion Embed
        if (isDailymotionUrl(trimmed)) {
            val embedUrl = getDailymotionEmbedUrl(trimmed, isMuted)
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                        iframe { width: 100%; height: 100%; border: 0; }
                    </style>
                </head>
                <body>
                    <iframe src="$embedUrl" allowfullscreen allow="autoplay; fullscreen"></iframe>
                </body>
                </html>
            """.trimIndent()
        }

        // If it's a direct HLS (.m3u8) or video stream in Web Mode, use HTML5 Video + Hls.js
        if (isHlsUrl(trimmed) || isDirectMediaStream(trimmed)) {
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                        video { width: 100%; height: 100%; object-fit: contain; background: #000; outline: none; }
                        .error-msg { color: #fff; text-align: center; font-family: sans-serif; padding-top: 25%; font-size: 14px; }
                    </style>
                    <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
                </head>
                <body>
                    <video id="videoPlayer" controls autoplay playsinline webkit-playsinline $isMuteString></video>
                    <div id="error" class="error-msg" style="display:none;">Não foi possível reproduzir este sinal de vídeo.</div>
                    <script>
                        var video = document.getElementById('videoPlayer');
                        var streamSrc = '$trimmed';
                        video.muted = $muteBool;
                        
                        function startPlayback() {
                            if (video.canPlayType('application/vnd.apple.mpegurl')) {
                                video.src = streamSrc;
                                video.play().catch(function(e) { console.log('Autoplay blocked:', e); });
                            } else if (typeof Hls !== 'undefined' && Hls.isSupported()) {
                                var hls = new Hls({
                                    enableWorker: true,
                                    lowLatencyMode: true,
                                    manifestLoadingMaxRetry: 4,
                                    levelLoadingMaxRetry: 4
                                });
                                hls.loadSource(streamSrc);
                                hls.attachMedia(video);
                                hls.on(Hls.Events.MANIFEST_PARSED, function() {
                                    video.play().catch(function(e) { console.log('Hls autoplay:', e); });
                                });
                                hls.on(Hls.Events.ERROR, function(event, data) {
                                    if (data.fatal) {
                                        switch (data.type) {
                                            case Hls.ErrorTypes.NETWORK_ERROR:
                                                hls.startLoad();
                                                break;
                                            case Hls.ErrorTypes.MEDIA_ERROR:
                                                hls.recoverMediaError();
                                                break;
                                            default:
                                                hls.destroy();
                                                document.getElementById('error').style.display = 'block';
                                                break;
                                        }
                                    }
                                });
                            } else {
                                video.src = streamSrc;
                                video.play().catch(function(e) {});
                            }
                        }
                        
                        window.onload = startPlayback;
                    </script>
                </body>
                </html>
            """.trimIndent()
        }

        // Generic Web Page or Embed Frame
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                    iframe { width: 100%; height: 100%; border: 0; display: block; }
                </style>
            </head>
            <body>
                <iframe 
                    src="$trimmed" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                    allowfullscreen>
                </iframe>
            </body>
            </html>
        """.trimIndent()
    }
}
