package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.TvChannel
import com.example.ui.theme.BrasilGreen
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeNavy
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceCard
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextWhite
import com.example.ui.theme.StarGold

@Composable
fun FavoritesScreen(
    channels: List<TvChannel>,
    watchHistory: List<WatchHistoryEntity>,
    currentChannel: TvChannel?,
    onChannelClick: (TvChannel) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onDeleteCustomChannel: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Minha Lista", "Continuar Assistindo", "Canais Personalizados")

    val favoriteChannels = channels.filter { it.isFavorite }
    val customChannels = channels.filter { it.isCustom }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimeNavy)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PrimeSurface,
            contentColor = PrimeBlue
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Favorites tab
                if (favoriteChannels.isEmpty()) {
                    EmptyFavoritesState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                    ) {
                        items(favoriteChannels, key = { it.id }) { ch ->
                            ChannelCardItem(
                                channel = ch,
                                isCurrentPlaying = currentChannel?.id == ch.id,
                                onClick = { onChannelClick(ch) },
                                onToggleFavorite = { onToggleFavorite(ch.id, ch.isFavorite) }
                            )
                        }
                    }
                }
            }

            1 -> {
                // Watch history tab
                if (watchHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Nenhum histórico recente",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Os canais que você assistir aparecerão aqui.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Assistidos Recentemente",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedButton(
                                    onClick = onClearHistory,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Limpar", fontSize = 12.sp)
                                }
                            }
                        }

                        items(watchHistory, key = { it.channelId + it.watchedAt }) { historyItem ->
                            val ch = channels.find { it.id == historyItem.channelId }
                            if (ch != null) {
                                ChannelCardItem(
                                    channel = ch,
                                    isCurrentPlaying = currentChannel?.id == ch.id,
                                    onClick = { onChannelClick(ch) },
                                    onToggleFavorite = { onToggleFavorite(ch.id, ch.isFavorite) }
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Custom channels tab
                if (customChannels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Nenhum canal adicionado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Você pode adicionar streams HLS (.m3u8) ou lives do YouTube usando o botão (+) na tela inicial.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                    ) {
                        items(customChannels, key = { it.id }) { ch ->
                            ChannelCardItem(
                                channel = ch,
                                isCurrentPlaying = currentChannel?.id == ch.id,
                                onClick = { onChannelClick(ch) },
                                onToggleFavorite = { onToggleFavorite(ch.id, ch.isFavorite) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = StarGold.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "Sua lista de favoritos está vazia",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Toque na estrela de qualquer canal na tela inicial para acessá-lo rapidamente por aqui.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
