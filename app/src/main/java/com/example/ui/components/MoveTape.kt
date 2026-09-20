package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MoveStep
import com.example.model.TurnType

@Composable
fun MoveTape(
    steps: List<MoveStep>,
    activeStepIndex: Int,
    onStepClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll to current active step
    LaunchedEffect(activeStepIndex) {
        if (activeStepIndex in steps.indices) {
            val targetPos = (activeStepIndex - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetPos)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .testTag("move_tape_row")
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(steps) { index, step ->
            val isActive = index == activeStepIndex

            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.08f else 1.0f,
                label = "step_scale"
            )

            val borderColor by animateColorAsState(
                targetValue = when {
                    isActive -> Color(0xFF00E5FF)
                    else -> Color(0xFF263047)
                },
                label = "step_border"
            )

            val backgroundBrush = if (isActive) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.28f),
                        Color(0xFF17283C)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2538),
                        Color(0xFF141926)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .testTag("move_step_${index}")
                    .scale(scale)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundBrush)
                    .border(
                        width = if (isActive) 2.dp else 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onStepClick(index) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .widthIn(min = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "#${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = if (isActive) Color(0xFF00E5FF) else Color(0xFF6E7D9C)
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = step.raw,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            color = if (isActive) Color.White else Color(0xFFE2E8F0)
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = step.spoken,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = if (isActive) Color(0xFFB3F5FF) else Color(0xFF8C9BAE)
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
