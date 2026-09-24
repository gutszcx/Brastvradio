package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProgramItem
import com.example.data.model.TvChannel
import com.example.ui.theme.BrasilGreen
import com.example.ui.theme.BrasilYellow
import com.example.ui.theme.LiveRed

@Composable
fun EpgScreen(
    programs: List<ProgramItem>,
    channels: List<TvChannel>,
    onSelectChannel: (TvChannel) -> Unit,
    onToggleReminder: (ProgramItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableStateOf("Hoje") }
    var selectedChannelId by remember { mutableStateOf<String?>(null) }

    val filteredPrograms = programs.filter { prog ->
        (selectedChannelId == null || prog.channelId == selectedChannelId) &&
                (prog.dayLabel == selectedDay)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // EPG Header & Day Selector
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
                        Text(
                            text = "Guia de Programação (EPG)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Acompanhe a grade horária das principais emissoras",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Day Selection Tabs (Hoje / Amanhã)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Hoje", "Amanhã").forEach { day ->
                        val isSelected = selectedDay == day
                        Surface(
                            onClick = { selectedDay = day },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BrasilGreen else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.testTag("epg_day_$day")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = day,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Channel Filter list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Todos os Canais"
                    Surface(
                        onClick = { selectedChannelId = null },
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedChannelId == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (selectedChannelId == null) androidx.compose.foundation.BorderStroke(1.dp, BrasilGreen) else null
                    ) {
                        Text(
                            text = "Todos os Canais",
                            fontSize = 12.sp,
                            fontWeight = if (selectedChannelId == null) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedChannelId == null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Individual Channels
                    channels.forEach { ch ->
                        val isSelected = selectedChannelId == ch.id
                        Surface(
                            onClick = { selectedChannelId = ch.id },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrasilGreen) else null
                        ) {
                            Text(
                                text = "${ch.number} ${ch.name}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Program List Items
        if (filteredPrograms.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum programa agendado para este dia/canal.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredPrograms, key = { it.id }) { program ->
                val matchingChannel = channels.find { it.id == program.channelId }
                EpgProgramCard(
                    program = program,
                    channel = matchingChannel,
                    onWatchNow = {
                        matchingChannel?.let { onSelectChannel(it) }
                    },
                    onToggleReminder = { onToggleReminder(program) }
                )
            }
        }
    }
}

@Composable
fun EpgProgramCard(
    program: ProgramItem,
    channel: TvChannel?,
    onWatchNow: () -> Unit,
    onToggleReminder: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (program.isLiveNow)
                BrasilGreen.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        border = if (program.isLiveNow)
            androidx.compose.foundation.BorderStroke(1.dp, BrasilGreen.copy(alpha = 0.6f))
        else
            androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("epg_program_${program.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Time, Live Badge, Channel Name, Age Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Time
                    Text(
                        text = "${program.startTime} - ${program.endTime}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (program.isLiveNow) BrasilGreen else MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (program.isLiveNow) {
                        Surface(
                            color = LiveRed,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NO AR",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Channel Name
                    Text(
                        text = "• ${program.channelName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Age Rating Badge (Brazilian standard)
                AgeRatingBadge(rating = program.ageRating)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Program Title
            Text(
                text = program.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Category tag
            Text(
                text = "${program.category} • ${program.durationMinutes} min",
                fontSize = 12.sp,
                color = BrasilYellow,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Synopsis
            Text(
                text = program.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Actions: Watch Now or Set Reminder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reminder button
                OutlinedButton(
                    onClick = onToggleReminder,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (program.hasReminder) BrasilGreen else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("reminder_btn_${program.id}")
                ) {
                    Icon(
                        imageVector = if (program.hasReminder) Icons.Default.AlarmOn else Icons.Default.Alarm,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (program.hasReminder) "Lembrete Ativo" else "Lembrar",
                        fontSize = 12.sp
                    )
                }

                if (program.isLiveNow && channel != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onWatchNow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrasilGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("watch_now_btn_${program.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Assistir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AgeRatingBadge(rating: String) {
    val (bgColor, textColor) = when (rating) {
        "L" -> Color(0xFF00C853) to Color.White
        "10" -> Color(0xFF00B0FF) to Color.White
        "12" -> Color(0xFFFFD600) to Color.Black
        "14" -> Color(0xFFFF6D00) to Color.White
        "16" -> Color(0xFFD50000) to Color.White
        "18" -> Color(0xFF212121) to Color.White
        else -> Color.Gray to Color.White
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = rating,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
