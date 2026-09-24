package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_channels")
data class FavoriteChannelEntity(
    @PrimaryKey val channelId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val channelId: String,
    val channelName: String,
    val watchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_channels")
data class CustomChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val streamUrl: String,
    val category: String,
    val isYoutube: Boolean,
    val number: String = "99.1"
)

@Entity(tableName = "program_reminders")
data class ProgramReminderEntity(
    @PrimaryKey val programId: String,
    val channelId: String,
    val programTitle: String,
    val startTime: String,
    val createdAt: Long = System.currentTimeMillis()
)
