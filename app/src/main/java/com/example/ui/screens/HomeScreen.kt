package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChannelCategory
import com.example.data.model.StreamProtocol
import com.example.data.model.TvChannel
import com.example.ui.components.AdMobInlineBanner
import com.example.ui.theme.BrasilGreen
import com.example.ui.theme.BrasilYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimeNavy
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceBorder
import com.example.ui.theme.PrimeSurfaceCard
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextWhite
import com.example.ui.theme.StarGold

@Composable
fun HomeScreen(
    channels: List<TvChannel>,
    currentChannel: TvChannel?,
    searchQuery: String,
    selectedCategory: ChannelCategory,
    selectedProtocol: StreamProtocol?,
    onlyFavorites: Boolean,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (ChannelCategory) -> Unit,
    onSelectProtocol: (StreamProtocol?) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onChannelClick: (TvChannel) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onAddCustomChannel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }

    // Filtered list for search or category filter mode
    val filteredChannels = remember(channels, searchQuery, selectedCategory, selectedProtocol, onlyFavorites) {
        channels.filter { ch ->
            val matchesQuery = searchQuery.isBlank() ||
                    ch.name.contains(searchQuery, ignoreCase = true) ||
                    ch.number.contains(searchQuery, ignoreCase = true) ||
                    ch.currentProgram.contains(searchQuery, ignoreCase = true) ||
                    ch.category.displayName.contains(searchQuery, ignoreCase = true) ||
                    ch.state.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == ChannelCategory.TODOS || ch.category == selectedCategory
            val matchesProtocol = selectedProtocol == null || ch.protocol == selectedProtocol
            val matchesFav = !onlyFavorites || ch.isFavorite

            matchesQuery && matchesCategory && matchesProtocol && matchesFav
        }
    }

    val isBrowsingSpecificFilter = searchQuery.isNotBlank() || selectedCategory != ChannelCategory.TODOS || selectedProtocol != null || onlyFavorites

    // Hero Spotlight Channel: defaults to first featured channel or current playing
    val spotlightChannel = remember(channels, currentChannel) {
        currentChannel ?: channels.firstOrNull()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimeNavy)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Prime Subnav Pills (Top Categories)
            item {
                PrimeCategorySubNav(
                    selectedCategory = selectedCategory,
                    onlyFavorites = onlyFavorites,
                    onSelectCategory = {
                        if (onlyFavorites) onToggleFavoritesOnly()
                        onSelectCategory(it)
                    },
                    onToggleFavorites = onToggleFavoritesOnly,
                    isSearchActive = searchQuery.isNotBlank() || isSearchExpanded,
                    onToggleSearch = { isSearchExpanded = !isSearchExpanded }
                )
            }

            // Expandable Prime Search Bar
            if (isSearchExpanded || searchQuery.isNotBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("channel_search_input"),
                            placeholder = {
                                Text("Buscar canais, programas, esportes ou notícias...", color = PrimeTextMuted, fontSize = 13.sp)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = PrimeBlue)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpar busca", tint = Color.White)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimeBlue,
                                unfocusedBorderColor = PrimeSurfaceBorder,
                                focusedContainerColor = PrimeSurfaceCard,
                                unfocusedContainerColor = PrimeSurfaceCard.copy(alpha = 0.8f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Format Filters Pill Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PrimeFormatFilterPill(
                                title = "Todos os Formatos",
                                selected = selectedProtocol == null,
                                onClick = { onSelectProtocol(null) }
                            )
                            PrimeFormatFilterPill(
                                title = "HLS (.m3u8)",
                                selected = selectedProtocol == StreamProtocol.HLS_M3U8,
                                onClick = { onSelectProtocol(StreamProtocol.HLS_M3U8) }
                            )
                            PrimeFormatFilterPill(
                                title = "YouTube Ao Vivo",
                                selected = selectedProtocol == StreamProtocol.YOUTUBE_LIVE,
                                onClick = { onSelectProtocol(StreamProtocol.YOUTUBE_LIVE) }
                            )
                            PrimeFormatFilterPill(
                                title = "Web Player / Embed",
                                selected = selectedProtocol == StreamProtocol.WEB_EMBED,
                                onClick = { onSelectProtocol(StreamProtocol.WEB_EMBED) }
                            )
                            PrimeFormatFilterPill(
                                title = "DASH (.mpd)",
                                selected = selectedProtocol == StreamProtocol.DASH_MPD,
                                onClick = { onSelectProtocol(StreamProtocol.DASH_MPD) }
                            )
                        }
                    }
                }
            }

            // Normal Prime Feed or Filter Results Mode
            if (isBrowsingSpecificFilter) {
                // Filtered List View
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Resultados (${filteredChannels.size} canais)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (filteredChannels.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Tv,
                                contentDescription = null,
                                tint = PrimeTextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhum canal encontrado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Tente alterar os termos da busca ou os filtros de formato.",
                                fontSize = 13.sp,
                                color = PrimeTextMuted
                            )
                        }
                    }
                } else {
                    items(filteredChannels, key = { it.id }) { channel ->
                        PrimeChannelRowCard(
                            channel = channel,
                            isCurrentPlaying = currentChannel?.id == channel.id,
                            onClick = { onChannelClick(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id, channel.isFavorite) }
                        )
                    }
                }
            } else {
                // Classic Amazon Prime Video Feed:
                // 1. Hero Feature Banner
                spotlightChannel?.let { channel ->
                    item {
                        PrimeHeroSpotlight(
                            channel = channel,
                            onWatchClick = { onChannelClick(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id, channel.isFavorite) }
                        )
                    }
                }

                // 2. Prime Rail: Top 10 Canais Mais Assistidos no Brasil
                item {
                    PrimeTop10Rail(
                        channels = channels.take(10),
                        currentChannel = currentChannel,
                        onChannelClick = onChannelClick,
                        onToggleFavorite = onToggleFavorite
                    )
                }

                // Non-intrusive AdMob Banner between rails
                item {
                    AdMobInlineBanner()
                }

                // 3. Prime Rail: Canais Ao Vivo em Destaque
                item {
                    PrimeContentRail(
                        title = "Canais Ao Vivo em Destaque",
                        badge = "PRIME TV",
                        channels = channels,
                        currentChannel = currentChannel,
                        onChannelClick = onChannelClick,
                        onToggleFavorite = onToggleFavorite
                    )
                }

                // 4. Prime Rail: Notícias & Jornalismo 24h
                val newsChannels = channels.filter { it.category == ChannelCategory.NOTICIAS }
                if (newsChannels.isNotEmpty()) {
                    item {
                        PrimeContentRail(
                            title = "Notícias & Jornalismo 24 Horas",
                            channels = newsChannels,
                            currentChannel = currentChannel,
                            onChannelClick = onChannelClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }

                // 5. Prime Rail: Esportes Ao Vivo & Resenha
                val sportsChannels = channels.filter { it.category == ChannelCategory.ESPORTES }
                if (sportsChannels.isNotEmpty()) {
                    item {
                        PrimeContentRail(
                            title = "Esportes Ao Vivo & Transmissões",
                            channels = sportsChannels,
                            currentChannel = currentChannel,
                            onChannelClick = onChannelClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }

                // 6. Prime Rail: Cultura, Conhecimento & Infantil
                val cultureChannels = channels.filter { it.category == ChannelCategory.CULTURA || it.category == ChannelCategory.INFANTIL }
                if (cultureChannels.isNotEmpty()) {
                    item {
                        PrimeContentRail(
                            title = "Cultura, Conhecimento & Educativos",
                            channels = cultureChannels,
                            currentChannel = currentChannel,
                            onChannelClick = onChannelClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }

                // 7. Prime Rail: Variedades & Religioso
                val entertainmentChannels = channels.filter { it.category == ChannelCategory.VARIEDADES || it.category == ChannelCategory.RELIGIOSO }
                if (entertainmentChannels.isNotEmpty()) {
                    item {
                        PrimeContentRail(
                            title = "Entretenimento & Variedades",
                            channels = entertainmentChannels,
                            currentChannel = currentChannel,
                            onChannelClick = onChannelClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }

        // Floating Action Button: Add Channel
        FloatingActionButton(
            onClick = onAddCustomChannel,
            containerColor = PrimeBlue,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
                .testTag("add_custom_channel_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Canal Próprio", tint = Color.Black)
        }
    }
}

/**
 * Amazon Prime Video Sub-Navigation Header:
 * "Todos", "TV Ao Vivo", "Notícias", "Esportes", "Cultura", "Minha Lista"
 */
@Composable
private fun PrimeCategorySubNav(
    selectedCategory: ChannelCategory,
    onlyFavorites: Boolean,
    onSelectCategory: (ChannelCategory) -> Unit,
    onToggleFavorites: () -> Unit,
    isSearchActive: Boolean,
    onToggleSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimeNavy)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Todos"
            PrimeSubNavPill(
                title = "Início",
                selected = selectedCategory == ChannelCategory.TODOS && !onlyFavorites,
                onClick = { onSelectCategory(ChannelCategory.TODOS) }
            )

            // "Ao Vivo" (Notícias / Geral)
            PrimeSubNavPill(
                title = "Notícias",
                selected = selectedCategory == ChannelCategory.NOTICIAS && !onlyFavorites,
                onClick = { onSelectCategory(ChannelCategory.NOTICIAS) }
            )

            // "Esportes"
            PrimeSubNavPill(
                title = "Esportes",
                selected = selectedCategory == ChannelCategory.ESPORTES && !onlyFavorites,
                onClick = { onSelectCategory(ChannelCategory.ESPORTES) }
            )

            // "Cultura"
            PrimeSubNavPill(
                title = "Cultura",
                selected = selectedCategory == ChannelCategory.CULTURA && !onlyFavorites,
                onClick = { onSelectCategory(ChannelCategory.CULTURA) }
            )

            // "Variedades"
            PrimeSubNavPill(
                title = "Variedades",
                selected = selectedCategory == ChannelCategory.VARIEDADES && !onlyFavorites,
                onClick = { onSelectCategory(ChannelCategory.VARIEDADES) }
            )

            // "Minha Lista" (Favorites)
            PrimeSubNavPill(
                title = "Minha Lista",
                selected = onlyFavorites,
                onClick = onToggleFavorites
            )
        }

        // Quick Search Toggle Icon Button
        IconButton(
            onClick = onToggleSearch,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = if (isSearchActive) PrimeBlue else Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PrimeSubNavPill(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) PrimeBlue else Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, PrimeSurfaceBorder)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.Black else Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PrimeFormatFilterPill(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) PrimeBlue.copy(alpha = 0.25f) else PrimeSurfaceCard,
        border = BorderStroke(1.dp, if (selected) PrimeBlue else PrimeSurfaceBorder)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) PrimeBlue else PrimeTextMuted,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

/**
 * Cinematic Prime Video Hero Spotlight Banner
 */
@Composable
private fun PrimeHeroSpotlight(
    channel: TvChannel,
    onWatchClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Hero Image Background
        Image(
            painter = painterResource(id = R.drawable.img_prime_hero_banner),
            contentDescription = "Prime Hero Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Amazon Prime Gradient: Bottom dark gradient fade + left vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            PrimeNavy.copy(alpha = 0.5f),
                            PrimeNavy
                        ),
                        startY = 60f
                    )
                )
        )

        // Hero Content Info
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Prime Live Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = LiveRed,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AO VIVO",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = PrimeBlue.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, PrimeBlue)
                ) {
                    Text(
                        text = "INCLUÍDO NO PRIME TV",
                        color = PrimeBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${channel.number} • ${channel.resolution}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Channel Title & Show
            Text(
                text = channel.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = channel.currentProgram,
                color = PrimeBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = channel.description,
                color = PrimeTextMuted,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // "Assistir Agora" Primary Button
                Button(
                    onClick = onWatchClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Assistir Agora",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // "+ Minha Lista" Glass Button
                Surface(
                    onClick = onToggleFavorite,
                    shape = RoundedCornerShape(8.dp),
                    color = PrimeSurfaceCard.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, PrimeSurfaceBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Minha Lista",
                            tint = if (channel.isFavorite) PrimeBlue else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (channel.isFavorite) "Na Lista" else "Minha Lista",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Amazon Prime Video Signature "Top 10 Brasil" Horizontal Rail
 */
@Composable
private fun PrimeTop10Rail(
    channels: List<TvChannel>,
    currentChannel: TvChannel?,
    onChannelClick: (TvChannel) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(top = 18.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = PrimeBlue,
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
            ) {}
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Top 10 Canais de TV no Brasil",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(channels, key = { _, ch -> "top10_${ch.id}" }) { index, channel ->
                PrimeTop10Card(
                    rank = index + 1,
                    channel = channel,
                    isCurrentPlaying = currentChannel?.id == channel.id,
                    onClick = { onChannelClick(channel) },
                    onToggleFavorite = { onToggleFavorite(channel.id, channel.isFavorite) }
                )
            }
        }
    }
}

/**
 * Prime Top 10 Card with large metallic number
 */
@Composable
private fun PrimeTop10Card(
    rank: Int,
    channel: TvChannel,
    isCurrentPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .testTag("top10_item_$rank"),
        verticalAlignment = Alignment.Bottom
    ) {
        // Large Stylized Prime Number (1, 2, 3...)
        Text(
            text = "$rank",
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = PrimeBlue.copy(alpha = 0.7f),
            letterSpacing = (-4).sp,
            modifier = Modifier.padding(end = 4.dp)
        )

        // 16:9 Landscape Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = PrimeSurfaceCard
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = if (isCurrentPlaying) 2.dp else 1.dp,
                color = if (isCurrentPlaying) PrimeBlue else PrimeSurfaceBorder
            ),
            modifier = Modifier
                .width(180.dp)
                .height(115.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Color banner header
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(channel.accentColorHex).copy(alpha = 0.4f),
                                    PrimeSurfaceCard
                                )
                            )
                        )
                )

                // Card info
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top row: Live dot & protocol
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = LiveRed,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = "AO VIVO",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }

                        Text(
                            text = channel.number,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bottom info
                    Column {
                        Text(
                            text = channel.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = channel.currentProgram,
                            color = PrimeTextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Prime Blue live progress indicator
                        LinearProgressIndicator(
                            progress = { channel.progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .clip(CircleShape),
                            color = PrimeBlue,
                            trackColor = PrimeSurfaceBorder
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard Prime Video Horizontal Content Rail
 */
@Composable
private fun PrimeContentRail(
    title: String,
    badge: String? = null,
    channels: List<TvChannel>,
    currentChannel: TvChannel?,
    onChannelClick: (TvChannel) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(top = 18.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = PrimeBlue,
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
            ) {}

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (badge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = PrimeBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, PrimeBlue)
                ) {
                    Text(
                        text = badge,
                        color = PrimeBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels, key = { "rail_${it.id}" }) { channel ->
                PrimeLandscapeChannelCard(
                    channel = channel,
                    isCurrentPlaying = currentChannel?.id == channel.id,
                    onClick = { onChannelClick(channel) },
                    onToggleFavorite = { onToggleFavorite(channel.id, channel.isFavorite) }
                )
            }
        }
    }
}

/**
 * 16:9 Landscape Card (Prime Video Standard)
 */
@Composable
private fun PrimeLandscapeChannelCard(
    channel: TvChannel,
    isCurrentPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = PrimeSurfaceCard
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (isCurrentPlaying) 2.dp else 1.dp,
            color = if (isCurrentPlaying) PrimeBlue else PrimeSurfaceBorder
        ),
        modifier = Modifier
            .width(200.dp)
            .height(125.dp)
            .testTag("prime_card_${channel.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(channel.accentColorHex).copy(alpha = 0.45f),
                                PrimeSurfaceCard
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(9.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = LiveRed,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = "AO VIVO",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Format Badge
                        val formatText = when (channel.protocol) {
                            StreamProtocol.HLS_M3U8 -> "HLS"
                            StreamProtocol.YOUTUBE_LIVE -> "YOUTUBE"
                            StreamProtocol.WEB_EMBED -> "WEB"
                            StreamProtocol.DASH_MPD -> "DASH"
                        }
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = formatText,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Favorite button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favoritar",
                            tint = if (channel.isFavorite) PrimeBlue else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Bottom channel details
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = channel.number,
                            color = PrimeBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = channel.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = channel.currentProgram,
                        color = PrimeTextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // Prime Blue progress bar
                    LinearProgressIndicator(
                        progress = { channel.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp)
                            .clip(CircleShape),
                        color = if (isCurrentPlaying) PrimeBlue else BrasilYellow,
                        trackColor = PrimeSurfaceBorder
                    )
                }
            }
        }
    }
}

/**
 * Prime Channel Row Card (used in search and filtered list)
 */
@Composable
private fun PrimeChannelRowCard(
    channel: TvChannel,
    isCurrentPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying) PrimeSurfaceCard.copy(alpha = 0.95f) else PrimeSurfaceCard
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (isCurrentPlaying) 1.5.dp else 1.dp,
            color = if (isCurrentPlaying) PrimeBlue else PrimeSurfaceBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("channel_item_${channel.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Number & Logo Box
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(channel.accentColorHex)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = channel.number,
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Text(
                        text = channel.logoText,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Channel & EPG info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = channel.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Protocol badge
                    val (badgeBg, badgeFg, badgeText) = when (channel.protocol) {
                        StreamProtocol.YOUTUBE_LIVE -> Triple(Color(0xFFFF0000).copy(alpha = 0.15f), LiveRed, "YT LIVE")
                        StreamProtocol.HLS_M3U8 -> Triple(PrimeBlue.copy(alpha = 0.15f), PrimeBlue, "HLS HD")
                        StreamProtocol.WEB_EMBED -> Triple(Color(0xFF1976D2).copy(alpha = 0.15f), Color(0xFF64B5F6), "WEB EMBED")
                        StreamProtocol.DASH_MPD -> Triple(Color(0xFFFFB300).copy(alpha = 0.15f), Color(0xFFFFC107), "DASH MPD")
                    }
                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeFg,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Current program info
                Text(
                    text = channel.currentProgram,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isCurrentPlaying) PrimeBlue else PrimeTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progress Bar for current show
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { channel.progressPercent },
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(CircleShape),
                        color = if (isCurrentPlaying) PrimeBlue else PrimeGold,
                        trackColor = PrimeSurfaceBorder
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = channel.currentProgramTime,
                        fontSize = 10.sp,
                        color = PrimeTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Actions: Favorite and Play icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (channel.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Favoritar",
                        tint = if (channel.isFavorite) PrimeBlue else PrimeTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isCurrentPlaying) {
                    Surface(
                        color = PrimeBlue,
                        shape = CircleShape,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Reproduzindo agora",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelCardItem(
    channel: TvChannel,
    isCurrentPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    PrimeChannelRowCard(
        channel = channel,
        isCurrentPlaying = isCurrentPlaying,
        onClick = onClick,
        onToggleFavorite = onToggleFavorite
    )
}

