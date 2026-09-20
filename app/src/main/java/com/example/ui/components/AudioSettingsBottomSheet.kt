package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioSettings
import com.example.model.PronunciationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSettingsBottomSheet(
    settings: AudioSettings,
    onSettingsChange: (AudioSettings) -> Unit,
    onTestMetronome: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131722),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF334155)) },
        modifier = Modifier.testTag("audio_settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Audio Drill Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                FilledTonalButton(
                    onClick = onTestMetronome,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF1E2638),
                        contentColor = Color(0xFF00E5FF)
                    ),
                    modifier = Modifier.testTag("test_click_button")
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Click", fontSize = 12.sp)
                }
            }

            HorizontalDivider(color = Color(0xFF222B3D))

            // Metronome woodblock tick toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Woodblock Metronome Click",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Audible synthetic woodblock pulse before moves & loops",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                    )
                }
                Switch(
                    checked = settings.metronomeTick,
                    onCheckedChange = { onSettingsChange(settings.copy(metronomeTick = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E5FF),
                        checkedTrackColor = Color(0xFF0D47A1),
                        uncheckedTrackColor = Color(0xFF1E2538)
                    ),
                    modifier = Modifier.testTag("metronome_tick_switch")
                )
            }

            // Loop Countdown toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Loop 3-2-1 Countdown",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Spoken 'three, two, one' rhythm before restarting loop",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                    )
                }
                Switch(
                    checked = settings.loopCountdown,
                    onCheckedChange = { onSettingsChange(settings.copy(loopCountdown = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E5FF),
                        checkedTrackColor = Color(0xFF0D47A1),
                        uncheckedTrackColor = Color(0xFF1E2538)
                    ),
                    modifier = Modifier.testTag("loop_countdown_switch")
                )
            }

            // Continuous Loop toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Repeat Algorithm (Loop)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Continuously repeat algorithm for muscle memory drilling",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                    )
                }
                Switch(
                    checked = settings.loop,
                    onCheckedChange = { onSettingsChange(settings.copy(loop = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E5FF),
                        checkedTrackColor = Color(0xFF0D47A1),
                        uncheckedTrackColor = Color(0xFF1E2538)
                    ),
                    modifier = Modifier.testTag("loop_switch")
                )
            }

            // Pronunciation Mode (Notation vs Descriptive)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pronunciation Mode",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Include ergonomic finger tricks in the audio voice",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PronunciationMode.entries.forEach { mode ->
                        val selected = settings.pronunciationMode == mode
                        Button(
                            onClick = { onSettingsChange(settings.copy(pronunciationMode = mode)) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFF00E5FF) else Color(0xFF1E2638),
                                contentColor = if (selected) Color(0xFF09111E) else Color(0xFFCBD5E1)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(mode.label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            // Step Delay in Paced Mode Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Paced Mode Step Pause",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "${settings.moveDelayMs} ms",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Slider(
                    value = settings.moveDelayMs.toFloat(),
                    onValueChange = { onSettingsChange(settings.copy(moveDelayMs = it.toLong())) },
                    valueRange = 100f..1200f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF),
                        inactiveTrackColor = Color(0xFF263047)
                    ),
                    modifier = Modifier.testTag("delay_slider")
                )
            }

            // Pitch Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Voice Pitch",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = String.format("%.1fx", settings.pitch),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Slider(
                    value = settings.pitch,
                    onValueChange = { onSettingsChange(settings.copy(pitch = it)) },
                    valueRange = 0.6f..1.5f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF),
                        inactiveTrackColor = Color(0xFF263047)
                    ),
                    modifier = Modifier.testTag("pitch_slider")
                )
            }
        }
    }
}
