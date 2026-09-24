package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.ChannelsData
import com.example.data.local.TvDatabase
import com.example.data.model.CastDevice
import com.example.data.model.ChannelCategory
import com.example.data.model.ProgramItem
import com.example.data.model.StreamProtocol
import com.example.data.model.TvChannel
import com.example.data.repository.ChannelRepository
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavTab(val title: String) {
    CANAIS("Início"),
    RADIOS("Rádios"),
    GUIA_EPG("Guia"),
    FAVORITOS("Favoritos"),
    TRANSMITIR("Cast"),
    AJUSTES("Ajustes")
}

enum class VideoAspect {
    FIT,
    FILL_16_9,
    ZOOM_STRETCH
}

data class TvUiState(
    val selectedTab: NavTab = NavTab.CANAIS,
    val searchQuery: String = "",
    val selectedCategory: ChannelCategory = ChannelCategory.TODOS,
    val selectedProtocolFilter: StreamProtocol? = null,
    val onlyFavorites: Boolean = false,
    val currentChannel: TvChannel? = null,
    val isPlaying: Boolean = true,
    val isMuted: Boolean = false,
    val videoAspect: VideoAspect = VideoAspect.FIT,
    val isFullscreen: Boolean = false,
    val sleepTimerMinutes: Int = 0,
    val sleepTimerRemainingSeconds: Int = 0,
    // Cast
    val isCasting: Boolean = false,
    val activeCastDevice: CastDevice? = null,
    val castVolume: Float = 0.8f,
    val availableCastDevices: List<CastDevice> = ChannelsData.sampleCastDevices,
    val isScanningCast: Boolean = false,
    // Settings
    val themeMode: ThemeMode = ThemeMode.DARK,
    val highContrast: Boolean = false,
    val largeText: Boolean = false,
    val autoPlayOnStart: Boolean = true,
    val notificationMessage: String? = null
)

class TvViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChannelRepository

    init {
        val database = TvDatabase.getDatabase(application)
        repository = ChannelRepository(database.tvDao())
    }

    private val _uiState = MutableStateFlow(TvUiState())
    val uiState: StateFlow<TvUiState> = _uiState.asStateFlow()

    val allChannels: StateFlow<List<TvChannel>> = repository.getAllChannelsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val epgPrograms: StateFlow<List<ProgramItem>> = repository.getEpgProgramsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChannelsData.samplePrograms)

    val watchHistory = repository.watchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var sleepTimerJob: Job? = null

    init {
        // Set first channel as initial current channel when loaded
        viewModelScope.launch {
            allChannels.collect { channels ->
                if (_uiState.value.currentChannel == null && channels.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(currentChannel = channels.first())
                }
            }
        }
    }

    fun selectTab(tab: NavTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectCategory(category: ChannelCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setProtocolFilter(protocol: StreamProtocol?) {
        _uiState.value = _uiState.value.copy(selectedProtocolFilter = protocol)
    }

    fun toggleFavoritesOnly() {
        _uiState.value = _uiState.value.copy(onlyFavorites = !_uiState.value.onlyFavorites)
    }

    fun playChannel(channel: TvChannel) {
        _uiState.value = _uiState.value.copy(
            currentChannel = channel,
            isPlaying = true
        )
        viewModelScope.launch {
            repository.recordWatch(channel.id, channel.name)
        }
    }

    fun togglePlayPause() {
        _uiState.value = _uiState.value.copy(isPlaying = !_uiState.value.isPlaying)
    }

    fun pauseVideo() {
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun playVideo() {
        _uiState.value = _uiState.value.copy(isPlaying = true)
    }

    fun toggleMute() {
        _uiState.value = _uiState.value.copy(isMuted = !_uiState.value.isMuted)
    }

    fun cycleAspectRatio() {
        val next = when (_uiState.value.videoAspect) {
            VideoAspect.FIT -> VideoAspect.FILL_16_9
            VideoAspect.FILL_16_9 -> VideoAspect.ZOOM_STRETCH
            VideoAspect.ZOOM_STRETCH -> VideoAspect.FIT
        }
        _uiState.value = _uiState.value.copy(videoAspect = next)
    }

    fun setFullscreen(fullscreen: Boolean) {
        _uiState.value = _uiState.value.copy(isFullscreen = fullscreen)
    }

    fun nextChannel() {
        val list = allChannels.value
        val current = _uiState.value.currentChannel ?: return
        val currentIndex = list.indexOfFirst { it.id == current.id }
        if (currentIndex != -1 && list.isNotEmpty()) {
            val nextIndex = (currentIndex + 1) % list.size
            playChannel(list[nextIndex])
        }
    }

    fun previousChannel() {
        val list = allChannels.value
        val current = _uiState.value.currentChannel ?: return
        val currentIndex = list.indexOfFirst { it.id == current.id }
        if (currentIndex != -1 && list.isNotEmpty()) {
            val prevIndex = if (currentIndex - 1 < 0) list.size - 1 else currentIndex - 1
            playChannel(list[prevIndex])
        }
    }

    fun toggleFavorite(channelId: String, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(channelId, isFav)
        }
    }

    fun toggleProgramReminder(program: ProgramItem) {
        viewModelScope.launch {
            repository.toggleProgramReminder(program)
            val msg = if (program.hasReminder) {
                "Lembrete removido para '${program.title}'"
            } else {
                "Lembrete ativado para '${program.title}' às ${program.startTime}"
            }
            showNotification(msg)
        }
    }

    fun addCustomChannel(name: String, url: String, category: String, isYoutube: Boolean) {
        viewModelScope.launch {
            repository.addCustomChannel(name, url, category, isYoutube)
            showNotification("Canal '$name' adicionado com sucesso!")
        }
    }

    fun deleteCustomChannel(id: String) {
        viewModelScope.launch {
            repository.deleteCustomChannel(id)
            showNotification("Canal removido.")
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            showNotification("Histórico limpo.")
        }
    }

    // Cast actions
    fun scanCastDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanningCast = true)
            delay(1200)
            _uiState.value = _uiState.value.copy(isScanningCast = false)
        }
    }

    fun connectCast(device: CastDevice) {
        _uiState.value = _uiState.value.copy(
            isCasting = true,
            activeCastDevice = device.copy(isConnected = true),
            availableCastDevices = _uiState.value.availableCastDevices.map {
                it.copy(isConnected = it.id == device.id)
            }
        )
        showNotification("Conectado a ${device.name}! Transmitindo...")
    }

    fun disconnectCast() {
        val dev = _uiState.value.activeCastDevice
        _uiState.value = _uiState.value.copy(
            isCasting = false,
            activeCastDevice = null,
            availableCastDevices = _uiState.value.availableCastDevices.map {
                it.copy(isConnected = false)
            }
        )
        if (dev != null) {
            showNotification("Transmissão em ${dev.name} encerrada.")
        }
    }

    fun setCastVolume(vol: Float) {
        _uiState.value = _uiState.value.copy(castVolume = vol.coerceIn(0f, 1f))
    }

    // Sleep Timer
    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _uiState.value = _uiState.value.copy(
                sleepTimerMinutes = 0,
                sleepTimerRemainingSeconds = 0
            )
            showNotification("Temporizador desativado.")
            return
        }

        val totalSeconds = minutes * 60
        _uiState.value = _uiState.value.copy(
            sleepTimerMinutes = minutes,
            sleepTimerRemainingSeconds = totalSeconds
        )
        showNotification("O player será desligado em $minutes minutos.")

        sleepTimerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.value = _uiState.value.copy(sleepTimerRemainingSeconds = remaining)
            }
            // Timer expired: stop playback
            _uiState.value = _uiState.value.copy(
                isPlaying = false,
                sleepTimerMinutes = 0,
                sleepTimerRemainingSeconds = 0
            )
            showNotification("Temporizador: Reprodução pausada automaticamente.")
        }
    }

    // Settings
    fun setThemeMode(mode: ThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun toggleHighContrast() {
        _uiState.value = _uiState.value.copy(highContrast = !_uiState.value.highContrast)
    }

    fun toggleLargeText() {
        _uiState.value = _uiState.value.copy(largeText = !_uiState.value.largeText)
    }

    fun showNotification(msg: String) {
        _uiState.value = _uiState.value.copy(notificationMessage = msg)
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(notificationMessage = null)
    }
}
