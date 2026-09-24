package com.example.data.repository

import com.example.data.datasource.ChannelsData
import com.example.data.local.CustomChannelEntity
import com.example.data.local.FavoriteChannelEntity
import com.example.data.local.ProgramReminderEntity
import com.example.data.local.TvDao
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.CastDevice
import com.example.data.model.ChannelCategory
import com.example.data.model.ProgramItem
import com.example.data.model.StreamProtocol
import com.example.data.model.TvChannel
import com.example.util.StreamUrlHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ChannelRepository(private val tvDao: TvDao) {

    val favoriteIds: Flow<List<String>> = tvDao.getFavoriteChannelIds()
    val watchHistory: Flow<List<WatchHistoryEntity>> = tvDao.getWatchHistory()
    val customChannels: Flow<List<CustomChannelEntity>> = tvDao.getCustomChannels()
    val reminderIds: Flow<List<String>> = tvDao.getReminderProgramIds()

    fun getAllChannelsFlow(): Flow<List<TvChannel>> {
        return combine(favoriteIds, customChannels) { favs, customs ->
            val favSet = favs.toSet()
            val customMapped = customs.map { c ->
                val protocol = StreamUrlHelper.detectProtocol(c.streamUrl, c.isYoutube)
                TvChannel(
                    id = c.id,
                    name = c.name,
                    number = c.number,
                    category = ChannelCategory.values().find { it.name == c.category } ?: ChannelCategory.VARIEDADES,
                    protocol = protocol,
                    streamUrl = c.streamUrl,
                    youtubeId = if (protocol == StreamProtocol.YOUTUBE_LIVE) c.streamUrl else null,
                    logoText = c.name.take(3).uppercase(),
                    accentColorHex = 0xFF7C4DFF,
                    state = "Canal Adicionado",
                    resolution = when (protocol) {
                        StreamProtocol.HLS_M3U8 -> "HLS Live"
                        StreamProtocol.DASH_MPD -> "DASH HD"
                        StreamProtocol.YOUTUBE_LIVE -> "YouTube Live"
                        StreamProtocol.WEB_EMBED -> "Web Player"
                    },
                    description = "Canal personalizado adicionado pelo usuário.",
                    websiteUrl = "",
                    currentProgram = "Transmissão Ao Vivo",
                    currentProgramTime = "Ao Vivo",
                    progressPercent = 0.5f,
                    isCustom = true,
                    isFavorite = favSet.contains(c.id)
                )
            }

            val defaultMapped = ChannelsData.defaultChannels.map { channel ->
                channel.copy(isFavorite = favSet.contains(channel.id))
            }

            defaultMapped + customMapped
        }
    }

    fun getEpgProgramsFlow(): Flow<List<ProgramItem>> {
        return reminderIds.combine(getAllChannelsFlow()) { reminders, channels ->
            val reminderSet = reminders.toSet()
            ChannelsData.samplePrograms.map { prog ->
                prog.copy(hasReminder = reminderSet.contains(prog.id))
            }
        }
    }

    suspend fun toggleFavorite(channelId: String, isFavoriteNow: Boolean) {
        if (isFavoriteNow) {
            tvDao.removeFavorite(channelId)
        } else {
            tvDao.addFavorite(FavoriteChannelEntity(channelId = channelId))
        }
    }

    suspend fun recordWatch(channelId: String, channelName: String) {
        tvDao.recordWatchHistory(WatchHistoryEntity(channelId = channelId, channelName = channelName))
    }

    suspend fun clearHistory() {
        tvDao.clearHistory()
    }

    suspend fun addCustomChannel(name: String, url: String, category: String, isYoutube: Boolean) {
        val id = "custom_" + System.currentTimeMillis()
        tvDao.insertCustomChannel(
            CustomChannelEntity(
                id = id,
                name = name,
                streamUrl = url,
                category = category,
                isYoutube = isYoutube
            )
        )
    }

    suspend fun deleteCustomChannel(id: String) {
        tvDao.deleteCustomChannel(id)
    }

    suspend fun toggleProgramReminder(program: ProgramItem) {
        if (program.hasReminder) {
            tvDao.removeReminder(program.id)
        } else {
            tvDao.addReminder(
                ProgramReminderEntity(
                    programId = program.id,
                    channelId = program.channelId,
                    programTitle = program.title,
                    startTime = program.startTime
                )
            )
        }
    }
}
