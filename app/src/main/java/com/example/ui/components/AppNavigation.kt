package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrasilGreen
import com.example.ui.theme.BrasilYellow
import com.example.ui.theme.CastPurple
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeNavy
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextWhite
import com.example.ui.viewmodel.NavTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrasilTvTopBar(
    isCasting: Boolean,
    sleepTimerMinutes: Int,
    onOpenCast: () -> Unit,
    onOpenSleepTimer: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Prime Video style brand logo: "prime tv" with signature smile curve
                Column(modifier = Modifier.padding(end = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "prime",
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp,
                            letterSpacing = (-0.5).sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = PrimeBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBlue.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "TV",
                                color = PrimeBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    // Prime Signature Smile Curve
                    Box(
                        modifier = Modifier
                            .width(54.dp)
                            .height(5.dp)
                            .drawBehind {
                                val path = Path().apply {
                                    moveTo(2f, 1f)
                                    quadraticBezierTo(
                                        size.width * 0.55f, size.height + 2f,
                                        size.width - 2f, 2f
                                    )
                                }
                                drawPath(
                                    path = path,
                                    color = PrimeBlue,
                                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                )
                            }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Live badge
                Surface(
                    color = LiveRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LiveRed.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(LiveRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AO VIVO",
                            color = LiveRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        actions = {
            // Sleep Timer Shortcut
            IconButton(
                onClick = onOpenSleepTimer,
                modifier = Modifier.testTag("topbar_timer_button")
            ) {
                Box {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Temporizador",
                        tint = if (sleepTimerMinutes > 0) BrasilYellow else Color.White.copy(alpha = 0.8f)
                    )
                    if (sleepTimerMinutes > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BrasilYellow, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // Chromecast Shortcut
            IconButton(
                onClick = onOpenCast,
                modifier = Modifier.testTag("topbar_cast_button")
            ) {
                Icon(
                    imageVector = if (isCasting) Icons.Default.CastConnected else Icons.Default.Cast,
                    contentDescription = "Transmitir para TV / Chromecast",
                    tint = if (isCasting) PrimeBlue else Color.White.copy(alpha = 0.8f)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimeNavy
        )
    )
}

@Composable
fun BrasilTvBottomNav(
    selectedTab: NavTab,
    isCasting: Boolean,
    onSelectTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = PrimeSurface,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = selectedTab == NavTab.CANAIS,
            onClick = { onSelectTab(NavTab.CANAIS) },
            icon = {
                Icon(
                    if (selectedTab == NavTab.CANAIS) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Início"
                )
            },
            label = { Text("Início", fontSize = 11.sp, fontWeight = if (selectedTab == NavTab.CANAIS) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = PrimeBlue,
                unselectedIconColor = PrimeTextMuted,
                unselectedTextColor = PrimeTextMuted,
                indicatorColor = PrimeBlue
            ),
            modifier = Modifier.testTag("nav_tab_canais")
        )

        NavigationBarItem(
            selected = selectedTab == NavTab.RADIOS,
            onClick = { onSelectTab(NavTab.RADIOS) },
            icon = {
                Icon(
                    if (selectedTab == NavTab.RADIOS) Icons.Filled.Radio else Icons.Outlined.Radio,
                    contentDescription = "Rádios"
                )
            },
            label = { Text("Rádios", fontSize = 11.sp, fontWeight = if (selectedTab == NavTab.RADIOS) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = PrimeBlue,
                unselectedIconColor = PrimeTextMuted,
                unselectedTextColor = PrimeTextMuted,
                indicatorColor = PrimeBlue
            ),
            modifier = Modifier.testTag("nav_tab_radios")
        )

        NavigationBarItem(
            selected = selectedTab == NavTab.GUIA_EPG,
            onClick = { onSelectTab(NavTab.GUIA_EPG) },
            icon = {
                Icon(
                    if (selectedTab == NavTab.GUIA_EPG) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                    contentDescription = "Guia EPG"
                )
            },
            label = { Text("Guia TV", fontSize = 11.sp, fontWeight = if (selectedTab == NavTab.GUIA_EPG) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = PrimeBlue,
                unselectedIconColor = PrimeTextMuted,
                unselectedTextColor = PrimeTextMuted,
                indicatorColor = PrimeBlue
            ),
            modifier = Modifier.testTag("nav_tab_epg")
        )

        NavigationBarItem(
            selected = selectedTab == NavTab.FAVORITOS,
            onClick = { onSelectTab(NavTab.FAVORITOS) },
            icon = {
                Icon(
                    if (selectedTab == NavTab.FAVORITOS) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Minha Lista"
                )
            },
            label = { Text("Minha Lista", fontSize = 11.sp, fontWeight = if (selectedTab == NavTab.FAVORITOS) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = PrimeBlue,
                unselectedIconColor = PrimeTextMuted,
                unselectedTextColor = PrimeTextMuted,
                indicatorColor = PrimeBlue
            ),
            modifier = Modifier.testTag("nav_tab_favoritos")
        )

        NavigationBarItem(
            selected = selectedTab == NavTab.TRANSMITIR,
            onClick = { onSelectTab(NavTab.TRANSMITIR) },
            icon = {
                Box {
                    Icon(
                        if (isCasting) Icons.Filled.CastConnected else Icons.Filled.Cast,
                        contentDescription = "Cast"
                    )
                    if (isCasting) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(PrimeBlue, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            },
            label = { Text("Transmitir", fontSize = 11.sp, fontWeight = if (selectedTab == NavTab.TRANSMITIR) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = PrimeBlue,
                unselectedIconColor = PrimeTextMuted,
                unselectedTextColor = PrimeTextMuted,
                indicatorColor = if (isCasting) CastPurple else PrimeBlue
            ),
            modifier = Modifier.testTag("nav_tab_cast")
        )

        NavigationBarItem(
            selected = selectedTab == NavTab.AJUSTES,
            onClick = { onSelectTab(NavTab.AJUSTES) },
            icon = {
                Icon(
                    if (selectedTab == NavTab.AJUSTES) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Ajustes"
                )
            },
            label = { Text("Ajustes", fontSize = 11.sp, fontWeight = if (selectedTab == NavTab.AJUSTES) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = PrimeBlue,
                unselectedIconColor = PrimeTextMuted,
                unselectedTextColor = PrimeTextMuted,
                indicatorColor = PrimeBlue
            ),
            modifier = Modifier.testTag("nav_tab_ajustes")
        )
    }
}

