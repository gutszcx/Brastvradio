package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.ChannelCategory
import com.example.data.model.StreamProtocol
import com.example.ui.theme.BrasilGreen
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeSurface
import com.example.util.StreamUrlHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomChannelDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, url: String, category: String, isYoutube: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("AUTO") }
    var selectedCategory by remember { mutableStateOf(ChannelCategory.VARIEDADES.name) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val detectedProtocol = remember(url, selectedType) {
        when (selectedType) {
            "HLS" -> StreamProtocol.HLS_M3U8
            "YOUTUBE" -> StreamProtocol.YOUTUBE_LIVE
            "EMBED" -> StreamProtocol.WEB_EMBED
            "DASH" -> StreamProtocol.DASH_MPD
            else -> if (url.isNotBlank()) StreamUrlHelper.detectProtocol(url) else StreamProtocol.HLS_M3U8
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PrimeSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LiveTv, contentDescription = null, tint = PrimeBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Canal Próprio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Suporta todos os formatos: streams HLS (.m3u8), DASH (.mpd), lives do YouTube e players web/embed.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stream Type Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "AUTO",
                        onClick = { selectedType = "AUTO" },
                        label = { Text("Automático") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrasilGreen,
                            selectedLabelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = selectedType == "HLS",
                        onClick = { selectedType = "HLS" },
                        label = { Text("HLS (.m3u8)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrasilGreen,
                            selectedLabelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = selectedType == "YOUTUBE",
                        onClick = { selectedType = "YOUTUBE" },
                        label = { Text("YouTube Live") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF0000),
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedType == "EMBED",
                        onClick = { selectedType = "EMBED" },
                        label = { Text("Player Web / Embed") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1976D2),
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedType == "DASH",
                        onClick = { selectedType = "DASH" },
                        label = { Text("DASH (.mpd)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFB300),
                            selectedLabelColor = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nome do Canal") },
                    placeholder = { Text("Ex: TV Web Brasil ou Canal Regional") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_channel_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        errorMessage = null
                    },
                    label = { Text("URL do Stream ou Embed") },
                    placeholder = { Text("https://...m3u8, /live, youtube.com ou embed iframe") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_channel_url_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                if (url.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BrasilGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Formato detectado: ${detectedProtocol.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Por favor, digite o nome do canal."
                        return@Button
                    }
                    if (url.isBlank()) {
                        errorMessage = "Por favor, digite a URL do stream."
                        return@Button
                    }
                    val isYt = detectedProtocol == StreamProtocol.YOUTUBE_LIVE
                    onAdd(name.trim(), url.trim(), selectedCategory, isYt)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimeBlue,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("custom_channel_confirm_button")
            ) {
                Text("Adicionar Canal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

