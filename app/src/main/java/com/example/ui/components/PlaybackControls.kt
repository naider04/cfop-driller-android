package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioSettings
import com.example.model.PlaybackStyle
import com.example.model.PronunciationMode

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    settings: AudioSettings,
    onPlayPauseToggle: () -> Unit,
    onRestart: () -> Unit,
    onPreviousStep: () -> Unit,
    onNextStep: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPlaybackStyleChange: (PlaybackStyle) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag("playback_controls_column")
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141926))
            .border(1.dp, Color(0xFF263047), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode Selector: Fluid vs Paced
        Row(
            modifier = Modifier
                .testTag("playback_style_row")
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Audio Style",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold
                )
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F131D))
                    .border(1.dp, Color(0xFF2A344E), RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                // Fluid Button
                val isFluid = settings.playbackStyle == PlaybackStyle.CONTINUOUS
                Box(
                    modifier = Modifier
                        .testTag("style_fluid_button")
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isFluid) Color(0xFF00E5FF) else Color.Transparent)
                        .clickable { onPlaybackStyleChange(PlaybackStyle.CONTINUOUS) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Fluid (Flow)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isFluid) FontWeight.Bold else FontWeight.Medium,
                            color = if (isFluid) Color(0xFF09111E) else Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    )
                }

                // Paced Button
                val isPaced = settings.playbackStyle == PlaybackStyle.PACED
                Box(
                    modifier = Modifier
                        .testTag("style_paced_button")
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isPaced) Color(0xFF00E5FF) else Color.Transparent)
                        .clickable { onPlaybackStyleChange(PlaybackStyle.PACED) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Paced (Step)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isPaced) FontWeight.Bold else FontWeight.Medium,
                            color = if (isPaced) Color(0xFF09111E) else Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Transport Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Restart
            IconButton(
                onClick = onRestart,
                modifier = Modifier
                    .testTag("restart_button")
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Restart from Beginning",
                    tint = Color(0xFF94A3B8)
                )
            }

            // Step Backward
            IconButton(
                onClick = onPreviousStep,
                modifier = Modifier
                    .testTag("prev_step_button")
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous Step",
                    tint = Color(0xFFE2E8F0)
                )
            }

            // Play / Pause Primary Button
            Button(
                onClick = onPlayPauseToggle,
                modifier = Modifier
                    .testTag("play_pause_button")
                    .size(68.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color(0xFFFF5252) else Color(0xFF00E5FF)
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause Drilling" else "Start Drilling",
                    tint = Color(0xFF09111E),
                    modifier = Modifier.size(34.dp)
                )
            }

            // Step Forward
            IconButton(
                onClick = onNextStep,
                modifier = Modifier
                    .testTag("next_step_button")
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next Step",
                    tint = Color(0xFFE2E8F0)
                )
            }

            // Settings Sheet Toggle
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .testTag("audio_settings_button")
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Drill Audio Settings",
                    tint = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Real-time speed slider
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Voice Speed (Instant)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    )
                }
                Text(
                    text = "${String.format("%.1f", settings.speed)}x",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                )
            }

            Slider(
                value = settings.speed,
                onValueChange = { onSpeedChange(it) },
                valueRange = 0.5f..2.5f,
                steps = 19, // 0.1 increments
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00E5FF),
                    activeTrackColor = Color(0xFF00E5FF),
                    inactiveTrackColor = Color(0xFF263047)
                ),
                modifier = Modifier
                    .testTag("speed_slider")
                    .fillMaxWidth()
            )

            // Speed preset pills for quick cubing speeds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(0.7f, 1.0f, 1.3f, 1.7f, 2.2f).forEach { preset ->
                    val isSelected = kotlin.math.abs(settings.speed - preset) < 0.05f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color(0xFF1E2538))
                            .clickable { onSpeedChange(preset) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${preset}x",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF8C9BAE),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
