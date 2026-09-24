package com.example.data.model

enum class RadioGenre(val displayName: String) {
    TODOS("Todos os Gêneros"),
    NOTICIAS("Notícias"),
    MPB("MPB & Bossa"),
    SERTANEJO("Sertanejo"),
    POP("Pop & Hits"),
    ROCK("Rock"),
    SAMBA_PAGODE("Samba & Pagode"),
    GOSPEL("Gospel & Fé"),
    ESPORTES("Esportes & Bola"),
    FLASHBACK("Flashback & 80s"),
    FORRO("Forró & Piseiro")
}

data class RadioStation(
    val id: String,
    val name: String,
    val dial: String,
    val cityState: String,
    val genre: RadioGenre,
    val streamUrl: String,
    val backupStreamUrl: String? = null,
    val logoText: String,
    val accentColorHex: Long,
    val currentShow: String,
    val bitrate: String = "128 kbps Stereo",
    val isFavorite: Boolean = false,
    val websiteUrl: String? = null
)
