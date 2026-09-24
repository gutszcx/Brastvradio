package com.example.data.model

enum class ChannelCategory(val displayName: String, val iconName: String) {
    TODOS("Todos", "Grid"),
    NOTICIAS("Notícias", "Newspaper"),
    CULTURA("Cultura & Arte", "Palette"),
    EDUCATIVO("Educativo", "School"),
    ESPORTES("Esportes", "SportsSoccer"),
    VARIEDADES("Variedades", "Tv"),
    INFANTIL("Infantil", "ChildCare"),
    RELIGIOSO("Religioso", "Church"),
    GOVERNO("Cidadania / Gov", "AccountBalance")
}

enum class StreamProtocol(val displayName: String) {
    HLS_M3U8("HLS (.m3u8)"),
    YOUTUBE_LIVE("YouTube"),
    WEB_EMBED("Web Embed"),
    DASH_MPD("DASH (.mpd)")
}

data class TvChannel(
    val id: String,
    val name: String,
    val number: String,
    val category: ChannelCategory,
    val protocol: StreamProtocol,
    val streamUrl: String,
    val youtubeId: String? = null,
    val logoText: String,
    val accentColorHex: Long = 0xFF00C853,
    val state: String = "Nacional",
    val resolution: String = "1080p FHD",
    val description: String = "",
    val websiteUrl: String = "",
    val currentProgram: String = "Transmissão Ao Vivo",
    val currentProgramTime: String = "Ao Vivo",
    val progressPercent: Float = 0.5f,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false
)
