package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {
    @Query("SELECT channelId FROM favorite_channels")
    fun getFavoriteChannelIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteChannelEntity)

    @Query("DELETE FROM favorite_channels WHERE channelId = :channelId")
    suspend fun removeFavorite(channelId: String)

    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC LIMIT 15")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordWatchHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history")
    suspend fun clearHistory()

    @Query("SELECT * FROM custom_channels")
    fun getCustomChannels(): Flow<List<CustomChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomChannel(channel: CustomChannelEntity)

    @Query("DELETE FROM custom_channels WHERE id = :id")
    suspend fun deleteCustomChannel(id: String)

    @Query("SELECT programId FROM program_reminders")
    fun getReminderProgramIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReminder(reminder: ProgramReminderEntity)

    @Query("DELETE FROM program_reminders WHERE programId = :programId")
    suspend fun removeReminder(programId: String)
}
