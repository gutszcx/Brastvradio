package com.example.data.model

data class ProgramItem(
    val id: String,
    val channelId: String,
    val channelName: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val category: String,
    val ageRating: String, // "L", "10", "12", "14", "16"
    val isLiveNow: Boolean = false,
    val dayLabel: String = "Hoje", // "Hoje", "Amanhã"
    val hasReminder: Boolean = false
)
