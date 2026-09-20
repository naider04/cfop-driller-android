package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmCase
import com.example.model.DiagramType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CubeVisualizer(
    case: AlgorithmCase,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .testTag("cube_visualizer_box")
            .size(140.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = if (isPlaying) listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.15f * glowAlpha),
                        Color(0xFF131722)
                    ) else listOf(
                        Color(0xFF1C2234),
                        Color(0xFF0F131D)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.5.dp,
                color = if (isPlaying) Color(0xFF00E5FF).copy(alpha = glowAlpha) else Color(0xFF2A334B),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        when (case.diagramType) {
            DiagramType.OLL_TOP -> {
                OllTopDiagram(yellowPattern = case.yellowPattern)
            }
            DiagramType.PLL_CYCLE -> {
                PllCycleDiagram(arrows = case.pllArrows)
            }
            DiagramType.F2L_SLOT -> {
                F2lSlotDiagram()
            }
            DiagramType.GENERIC_CUBE -> {
                IsometricCubeDiagram(accentColor = Color(0xFF00E5FF))
            }
        }
    }
}

@Composable
private fun OllTopDiagram(yellowPattern: List<Boolean>) {
    val pattern = if (yellowPattern.size == 9) yellowPattern else List(9) { it == 4 }
    val yellowColor = Color(0xFFFFD600)
    val darkColor = Color(0xFF262C3A)

    Canvas(modifier = Modifier.size(90.dp)) {
        val cellSize = size.width / 3.2f
        val gap = (size.width - cellSize * 3) / 2

        for (row in 0..2) {
            for (col in 0..2) {
                val index = row * 3 + col
                val isYellow = pattern[index]
                val left = col * (cellSize + gap)
                val top = row * (cellSize + gap)

                drawRoundRect(
                    color = if (isYellow) yellowColor else darkColor,
                    topLeft = Offset(left, top),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Subtle border
                drawRoundRect(
                    color = Color(0xFF10141E),
                    topLeft = Offset(left, top),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun PllCycleDiagram(arrows: List<Pair<Int, Int>>) {
    // 8 outer positions around a 3x3 perimeter:
    // 0: top-left corner, 1: top edge, 2: top-right corner
    // 3: right edge, 4: bottom-right corner, 5: bottom edge
    // 6: bottom-left corner, 7: left edge
    val primaryCyan = Color(0xFF00E5FF)
    val yellowFace = Color(0xFFFFD600)

    Canvas(modifier = Modifier.size(90.dp)) {
        val cellSize = size.width / 3.2f
        val gap = (size.width - cellSize * 3) / 2

        // Draw solved yellow top face
        for (row in 0..2) {
            for (col in 0..2) {
                val left = col * (cellSize + gap)
                val top = row * (cellSize + gap)
                drawRoundRect(
                    color = yellowFace.copy(alpha = 0.85f),
                    topLeft = Offset(left, top),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF10141E),
                    topLeft = Offset(left, top),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
        }

        fun getPerimeterCenter(pos: Int): Offset {
            val (col, row) = when (pos) {
                0 -> Pair(0, 0)
                1 -> Pair(1, 0)
                2 -> Pair(2, 0)
                3 -> Pair(2, 1)
                4 -> Pair(2, 2)
                5 -> Pair(1, 2)
                6 -> Pair(0, 2)
                7 -> Pair(0, 1)
                else -> Pair(1, 1)
            }
            val x = col * (cellSize + gap) + cellSize / 2
            val y = row * (cellSize + gap) + cellSize / 2
            return Offset(x, y)
        }

        // Draw permutation swap lines/arrows
        arrows.forEach { (from, to) ->
            val p1 = getPerimeterCenter(from)
            val p2 = getPerimeterCenter(to)

            drawLine(
                color = primaryCyan,
                start = p1,
                end = p2,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color.White,
                radius = 3.5.dp.toPx(),
                center = p1
            )
            drawCircle(
                color = primaryCyan,
                radius = 4.dp.toPx(),
                center = p2
            )
        }
    }
}

@Composable
private fun F2lSlotDiagram() {
    val greenFront = Color(0xFF00E676)
    val redRight = Color(0xFFFF5252)
    val whiteBottom = Color(0xFFE0E0E0)
    val slotGlow = Color(0xFF00E5FF)

    Canvas(modifier = Modifier.size(90.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val cy = h / 2

        // Draw stylized isometric F2L corner/edge slot
        val frontFace = Path().apply {
            moveTo(cx - 30.dp.toPx(), cy - 5.dp.toPx())
            lineTo(cx, cy + 12.dp.toPx())
            lineTo(cx, cy + 38.dp.toPx())
            lineTo(cx - 30.dp.toPx(), cy + 20.dp.toPx())
            close()
        }
        drawPath(frontFace, greenFront.copy(alpha = 0.85f))

        val rightFace = Path().apply {
            moveTo(cx, cy + 12.dp.toPx())
            lineTo(cx + 30.dp.toPx(), cy - 5.dp.toPx())
            lineTo(cx + 30.dp.toPx(), cy + 20.dp.toPx())
            lineTo(cx, cy + 38.dp.toPx())
            close()
        }
        drawPath(rightFace, redRight.copy(alpha = 0.85f))

        val topFace = Path().apply {
            moveTo(cx, cy - 22.dp.toPx())
            lineTo(cx + 30.dp.toPx(), cy - 5.dp.toPx())
            lineTo(cx, cy + 12.dp.toPx())
            lineTo(cx - 30.dp.toPx(), cy - 5.dp.toPx())
            close()
        }
        drawPath(topFace, whiteBottom)

        // Highlight the FR slot target
        drawCircle(
            color = slotGlow,
            radius = 6.dp.toPx(),
            center = Offset(cx + 12.dp.toPx(), cy + 14.dp.toPx())
        )
    }
}

@Composable
private fun IsometricCubeDiagram(accentColor: Color) {
    Canvas(modifier = Modifier.size(80.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = 26.dp.toPx()

        drawCircle(
            color = accentColor.copy(alpha = 0.2f),
            radius = r,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = accentColor,
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
