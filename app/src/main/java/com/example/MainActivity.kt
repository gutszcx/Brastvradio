package com.example

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.RadioPlaybackService
import com.example.ui.components.BrasilTvBottomNav
import com.example.ui.components.BrasilTvTopBar
import com.example.ui.components.SleepTimerDialog
import com.example.ui.player.CastBottomSheet
import com.example.ui.player.VideoPlayerView
import com.example.ui.screens.AddCustomChannelDialog
import com.example.ui.screens.CastCenterScreen
import com.example.ui.screens.EpgScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RadiosScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BrasilTvTheme
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.TvViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val channels by viewModel.allChannels.collectAsStateWithLifecycle()
            val epgPrograms by viewModel.epgPrograms.collectAsStateWithLifecycle()
            val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()

            val snackbarHostState = remember { SnackbarHostState() }
            var showCastSheet by remember { mutableStateOf(false) }
            var showSleepTimerDialog by remember { mutableStateOf(false) }
            var showAddChannelDialog by remember { mutableStateOf(false) }

            // Handle snackbar notifications
            LaunchedEffect(uiState.notificationMessage) {
                uiState.notificationMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearNotification()
                }
            }

            // Back handler for fullscreen
            BackHandler(enabled = uiState.isFullscreen) {
                viewModel.setFullscreen(false)
            }

            BrasilTvTheme(
                themeMode = uiState.themeMode,
                highContrast = uiState.highContrast
            ) {
                if (uiState.isFullscreen) {
                    // Fullscreen Video Player Mode
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        VideoPlayerView(
                            channel = uiState.currentChannel,
                            isPlaying = uiState.isPlaying,
                            isMuted = uiState.isMuted,
                            videoAspect = uiState.videoAspect,
                            isFullscreen = true,
                            isCasting = uiState.isCasting,
                            sleepTimerRemainingSeconds = uiState.sleepTimerRemainingSeconds,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onToggleMute = { viewModel.toggleMute() },
                            onToggleFullscreen = { viewModel.setFullscreen(false) },
                            onCycleAspect = { viewModel.cycleAspectRatio() },
                            onNextChannel = { viewModel.nextChannel() },
                            onPrevChannel = { viewModel.previousChannel() },
                            onOpenCast = { showCastSheet = true },
                            onOpenSleepTimer = { showSleepTimerDialog = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Standard Layout with TopBar, Embedded Video Player, Screen Content, BottomNav
                    Scaffold(
                        topBar = {
                            BrasilTvTopBar(
                                isCasting = uiState.isCasting,
                                sleepTimerMinutes = uiState.sleepTimerMinutes,
                                onOpenCast = { showCastSheet = true },
                                onOpenSleepTimer = { showSleepTimerDialog = true }
                            )
                        },
                        bottomBar = {
                            BrasilTvBottomNav(
                                selectedTab = uiState.selectedTab,
                                isCasting = uiState.isCasting,
                                onSelectTab = { viewModel.selectTab(it) }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Persistent Live Video Player (shown when browsing TV content)
                            if (uiState.selectedTab != NavTab.RADIOS) {
                                VideoPlayerView(
                                    channel = uiState.currentChannel,
                                    isPlaying = uiState.isPlaying,
                                    isMuted = uiState.isMuted,
                                    videoAspect = uiState.videoAspect,
                                    isFullscreen = false,
                                    isCasting = uiState.isCasting,
                                    sleepTimerRemainingSeconds = uiState.sleepTimerRemainingSeconds,
                                    onTogglePlayPause = { viewModel.togglePlayPause() },
                                    onToggleMute = { viewModel.toggleMute() },
                                    onToggleFullscreen = { viewModel.setFullscreen(true) },
                                    onCycleAspect = { viewModel.cycleAspectRatio() },
                                    onNextChannel = { viewModel.nextChannel() },
                                    onPrevChannel = { viewModel.previousChannel() },
                                    onOpenCast = { showCastSheet = true },
                                    onOpenSleepTimer = { showSleepTimerDialog = true }
                                )
                            }

                            // Main Tab Content
                            Box(modifier = Modifier.weight(1f)) {
                                when (uiState.selectedTab) {
                                    NavTab.CANAIS -> {
                                        HomeScreen(
                                            channels = channels,
                                            currentChannel = uiState.currentChannel,
                                            searchQuery = uiState.searchQuery,
                                            selectedCategory = uiState.selectedCategory,
                                            selectedProtocol = uiState.selectedProtocolFilter,
                                            onlyFavorites = uiState.onlyFavorites,
                                            onSearchChange = { viewModel.setSearchQuery(it) },
                                            onSelectCategory = { viewModel.selectCategory(it) },
                                            onSelectProtocol = { viewModel.setProtocolFilter(it) },
                                            onToggleFavoritesOnly = { viewModel.toggleFavoritesOnly() },
                                            onChannelClick = {
                                                RadioPlaybackService.stopRadio(this@MainActivity)
                                                viewModel.playChannel(it)
                                            },
                                            onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                                            onAddCustomChannel = { showAddChannelDialog = true }
                                        )
                                    }

                                    NavTab.RADIOS -> {
                                        RadiosScreen(
                                            onRadioStarted = { viewModel.pauseVideo() }
                                        )
                                    }

                                    NavTab.GUIA_EPG -> {
                                        EpgScreen(
                                            programs = epgPrograms,
                                            channels = channels,
                                            onSelectChannel = {
                                                RadioPlaybackService.stopRadio(this@MainActivity)
                                                viewModel.playChannel(it)
                                            },
                                            onToggleReminder = { viewModel.toggleProgramReminder(it) }
                                        )
                                    }

                                    NavTab.FAVORITOS -> {
                                        FavoritesScreen(
                                            channels = channels,
                                            watchHistory = watchHistory,
                                            currentChannel = uiState.currentChannel,
                                            onChannelClick = {
                                                RadioPlaybackService.stopRadio(this@MainActivity)
                                                viewModel.playChannel(it)
                                            },
                                            onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                                            onDeleteCustomChannel = { viewModel.deleteCustomChannel(it) },
                                            onClearHistory = { viewModel.clearHistory() }
                                        )
                                    }

                                    NavTab.TRANSMITIR -> {
                                        CastCenterScreen(
                                            isCasting = uiState.isCasting,
                                            activeDevice = uiState.activeCastDevice,
                                            availableDevices = uiState.availableCastDevices,
                                            isScanning = uiState.isScanningCast,
                                            currentChannel = uiState.currentChannel,
                                            isPlaying = uiState.isPlaying,
                                            castVolume = uiState.castVolume,
                                            onScan = { viewModel.scanCastDevices() },
                                            onConnectDevice = { viewModel.connectCast(it) },
                                            onDisconnect = { viewModel.disconnectCast() },
                                            onTogglePlayPause = { viewModel.togglePlayPause() },
                                            onNextChannel = { viewModel.nextChannel() },
                                            onPrevChannel = { viewModel.previousChannel() },
                                            onSetVolume = { viewModel.setCastVolume(it) }
                                        )
                                    }

                                    NavTab.AJUSTES -> {
                                        SettingsScreen(
                                            currentThemeMode = uiState.themeMode,
                                            highContrast = uiState.highContrast,
                                            largeText = uiState.largeText,
                                            sleepTimerMinutes = uiState.sleepTimerMinutes,
                                            onSetThemeMode = { viewModel.setThemeMode(it) },
                                            onToggleHighContrast = { viewModel.toggleHighContrast() },
                                            onToggleLargeText = { viewModel.toggleLargeText() },
                                            onOpenSleepTimer = { showSleepTimerDialog = true },
                                            onOpenCast = { showCastSheet = true }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Cast Bottom Sheet
                if (showCastSheet) {
                    CastBottomSheet(
                        isCasting = uiState.isCasting,
                        activeDevice = uiState.activeCastDevice,
                        availableDevices = uiState.availableCastDevices,
                        isScanning = uiState.isScanningCast,
                        currentChannel = uiState.currentChannel,
                        isPlaying = uiState.isPlaying,
                        castVolume = uiState.castVolume,
                        onScan = { viewModel.scanCastDevices() },
                        onConnectDevice = {
                            viewModel.connectCast(it)
                            showCastSheet = false
                        },
                        onDisconnect = { viewModel.disconnectCast() },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onNextChannel = { viewModel.nextChannel() },
                        onPrevChannel = { viewModel.previousChannel() },
                        onSetVolume = { viewModel.setCastVolume(it) },
                        onDismiss = { showCastSheet = false }
                    )
                }

                // Sleep Timer Dialog
                if (showSleepTimerDialog) {
                    SleepTimerDialog(
                        currentMinutes = uiState.sleepTimerMinutes,
                        onSetTimer = { viewModel.setSleepTimer(it) },
                        onDismiss = { showSleepTimerDialog = false }
                    )
                }

                // Add Custom Channel Dialog
                if (showAddChannelDialog) {
                    AddCustomChannelDialog(
                        onDismiss = { showAddChannelDialog = false },
                        onAdd = { name, url, category, isYoutube ->
                            viewModel.addCustomChannel(name, url, category, isYoutube)
                        }
                    )
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(params)
            } catch (_: Exception) {
            }
        }
    }
}
