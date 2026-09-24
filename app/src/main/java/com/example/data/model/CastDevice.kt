package com.example.data.model

data class CastDevice(
    val id: String,
    val name: String,
    val type: String, // "Chromecast", "Google TV", "Smart TV", "Fire TV"
    val room: String, // "Sala de Estar", "Quarto", "Cozinha"
    val ipAddress: String,
    val isConnected: Boolean = false
)
