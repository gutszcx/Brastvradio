package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.RadioStationsData
import com.example.data.model.RadioGenre
import com.example.data.model.RadioStation
import com.example.service.RadioPlaybackService
import com.example.ui.components.AdMobInlineBanner
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeNavy
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceBorder
import com.example.ui.theme.PrimeSurfaceCard
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextWhite

@Composable
fun RadiosScreen(
    onRadioStarted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf(RadioGenre.TODOS) }

    val currentRadio by RadioPlaybackService.currentRadio.collectAsState()
    val isPlaying by RadioPlaybackService.isPlaying.collectAsState()
    val isBuffering by RadioPlaybackService.isBuffering.collectAsState()
    val radioVolume by RadioPlaybackService.radioVolume.collectAsState()

    val allRadios = RadioStationsData.sampleRadios

    val filteredRadios = remember(searchQuery, selectedGenre) {
        allRadios.filter { radio ->
            val matchesGenre = selectedGenre == RadioGenre.TODOS || radio.genre == selectedGenre
            val matchesSearch = searchQuery.isBlank() ||
                radio.name.contains(searchQuery, ignoreCase = true) ||
                radio.dial.contains(searchQuery, ignoreCase = true) ||
                radio.cityState.contains(searchQuery, ignoreCase = true) ||
                radio.genre.displayName.contains(searchQuery, ignoreCase = true)
            matchesGenre && matchesSearch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimeNavy)
            .testTag("radios_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (currentRadio != null) 120.dp else 24.dp)
        ) {
            // Header: Title & Lock Screen Feature Badge
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Radio,
                                    contentDescription = null,
                                    tint = PrimeBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rádios Online Brasil",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Todas as rádios AM/FM e Web • Áudio com tela bloqueada",
                                fontSize = 12.sp,
                                color = PrimeTextMuted,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Lockscreen Ready Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimeBlue.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, PrimeBlue.copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = PrimeBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TELA BLOQUEADA",
                                    color = PrimeBlue,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("radio_search_input"),
                        placeholder = {
                            Text(
                                "Buscar por nome, frequência (ex: 94.7) ou cidade...",
                                color = PrimeTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PrimeTextMuted)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpar", tint = PrimeTextMuted)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PrimeSurfaceCard,
                            unfocusedContainerColor = PrimeSurfaceCard,
                            focusedBorderColor = PrimeBlue,
                            unfocusedBorderColor = PrimeSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }

            // Genre Chips (Horizontal Scroll)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioGenre.values().forEach { genre ->
                        val isSelected = selectedGenre == genre
                        Surface(
                            modifier = Modifier
                                .clickable { selectedGenre = genre }
                                .testTag("radio_genre_${genre.name}"),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PrimeBlue else PrimeSurfaceCard,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PrimeBlue else PrimeSurfaceBorder
                            )
                        ) {
                            Text(
                                text = genre.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Featured Station Rail (Top 6 Brasil)
            if (searchQuery.isBlank() && selectedGenre == RadioGenre.TODOS) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "Rádios Mais Ouvidas no Brasil",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(allRadios.take(7)) { radio ->
                                val isCurrent = currentRadio?.id == radio.id
                                FeaturedRadioCard(
                                    radio = radio,
                                    isPlaying = isCurrent && isPlaying,
                                    isBuffering = isCurrent && isBuffering,
                                    onClick = {
                                        onRadioStarted()
                                        RadioPlaybackService.playRadio(context, radio)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Non-intrusive AdMob Inline Banner (Placed naturally between rails)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                AdMobInlineBanner(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section Header: Station List
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedGenre == RadioGenre.TODOS) "Todas as Emissoras (${filteredRadios.size})" else "${selectedGenre.displayName} (${filteredRadios.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Ao Vivo • Streaming Contínuo",
                        fontSize = 11.sp,
                        color = PrimeBlue
                    )
                }
            }

            // Stations List
            items(filteredRadios, key = { it.id }) { radio ->
                val isCurrent = currentRadio?.id == radio.id
                RadioStationRow(
                    radio = radio,
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && isPlaying,
                    isBuffering = isCurrent && isBuffering,
                    onPlay = {
                        onRadioStarted()
                        RadioPlaybackService.playRadio(context, radio)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Floating / Docked Mini Radio Controller (Appears when station selected)
        if (currentRadio != null) {
            RadioBottomDock(
                station = currentRadio!!,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                volume = radioVolume,
                onTogglePlayPause = { RadioPlaybackService.togglePlayPause(context) },
                onVolumeChange = { RadioPlaybackService.setVolume(it) },
                onNextStation = {
                    val all = RadioStationsData.sampleRadios
                    val currentIndex = all.indexOfFirst { it.id == currentRadio!!.id }
                    if (currentIndex != -1) {
                        val next = all[(currentIndex + 1) % all.size]
                        RadioPlaybackService.playRadio(context, next)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .testTag("radio_bottom_dock")
            )
        }
    }
}

@Composable
private fun FeaturedRadioCard(
    radio: RadioStation,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(170.dp)
            .height(130.dp)
            .clickable { onClick() }
            .testTag("featured_radio_${radio.id}"),
        shape = RoundedCornerShape(12.dp),
        color = PrimeSurfaceCard,
        border = BorderStroke(
            1.5.dp,
            if (isPlaying) PrimeBlue else PrimeSurfaceBorder
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Badge with station accent color
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(radio.accentColorHex),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = radio.logoText.take(4),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    if (isPlaying) {
                        LiveWaveVisualizer(modifier = Modifier.size(32.dp, 18.dp))
                    } else if (isBuffering) {
                        CircularProgressIndicator(
                            color = PrimeBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = radio.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${radio.dial} • ${radio.cityState.substringBefore(" -")}",
                        color = PrimeTextMuted,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun RadioStationRow(
    radio: RadioStation,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlay: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onPlay() }
            .testTag("radio_station_row_${radio.id}"),
        shape = RoundedCornerShape(12.dp),
        color = if (isCurrent) PrimeSurfaceCard.copy(alpha = 0.95f) else PrimeSurfaceCard,
        border = BorderStroke(
            1.dp,
            if (isCurrent) PrimeBlue else PrimeSurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Station Logo Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(radio.accentColorHex),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = radio.logoText.take(5),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = radio.name,
                            color = if (isCurrent) PrimeBlue else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            border = BorderStroke(0.5.dp, PrimeSurfaceBorder)
                        ) {
                            Text(
                                text = radio.dial,
                                color = PrimeTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = radio.currentShow,
                        color = PrimeTextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 3.dp)
                    ) {
                        Text(
                            text = radio.cityState,
                            color = PrimeTextMuted.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                        Text(
                            text = " • ",
                            color = PrimeTextMuted.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                        Text(
                            text = radio.genre.displayName,
                            color = PrimeBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action: Play/Pause/Buffering icon
            Surface(
                shape = CircleShape,
                color = if (isCurrent && isPlaying) PrimeBlue else PrimeSurfaceBorder.copy(alpha = 0.5f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isCurrent && isBuffering) {
                        CircularProgressIndicator(
                            color = PrimeBlue,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (isCurrent && isPlaying) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pausar",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Tocar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Docked mini radio player with volume, lock screen notification indicator, and waveform visualizer.
 */
@Composable
private fun RadioBottomDock(
    station: RadioStation,
    isPlaying: Boolean,
    isBuffering: Boolean,
    volume: Float,
    onTogglePlayPause: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onNextStation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = PrimeSurface,
        border = BorderStroke(1.dp, PrimeBlue.copy(alpha = 0.5f)),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Lock screen status banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = PrimeBlue,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tela Bloqueada Habilitada • Áudio contínuo",
                        color = PrimeBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = station.bitrate,
                    color = PrimeTextMuted,
                    fontSize = 10.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(station.accentColorHex),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = station.logoText.take(4),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = station.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${station.dial} • ${station.currentShow}",
                            color = PrimeTextMuted,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                // Controls: Visualizer, Play/Pause, Next
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isPlaying) {
                        LiveWaveVisualizer(modifier = Modifier.size(34.dp, 20.dp))
                    }

                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(42.dp)
                            .background(PrimeBlue, CircleShape)
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onNextStation,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Próxima Rádio",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated real-time waveform bars mimicking an audio frequency visualizer.
 */
@Composable
private fun LiveWaveVisualizer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "wave_anim")
    val anim1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b1"
    )
    val anim2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b2"
    )
    val anim3 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b3"
    )
    val anim4 by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(360, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b4"
    )

    Canvas(modifier = modifier) {
        val barWidth = size.width / 7f
        val heights = listOf(anim1, anim2, anim3, anim4)

        heights.forEachIndexed { index, ratio ->
            val barHeight = size.height * ratio
            val x = index * (barWidth + 3.dp.toPx())
            val y = size.height - barHeight
            drawRoundRect(
                color = Color(0xFF00A8E1),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }
    }
}
