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
                F2lSlotDiagram(facelets = case.f2lFacelets)
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
private fun F2lSlotDiagram(facelets: String?) {
    val slotGlow = Color(0xFF00E5FF)

    if (facelets == null || facelets.length < 27) {
        IsometricCubeDiagram(accentColor = slotGlow)
        return
    }

    val uFace = facelets.substring(0, 9)
    val fFace = facelets.substring(9, 18)
    val rFace = facelets.substring(18, 27)

    Canvas(modifier = Modifier.size(96.dp)) {
        val s = size.width

        // Iso affine projection. World: +x = right, +y = up, +z = front.
        val ex = Offset( 0.16f * s, -0.05f * s)
        val ez = Offset(-0.07f * s,  0.13f * s)
        val ey = Offset( 0.00f * s, -0.22f * s)
        val origin = Offset(0.50f * s, 0.62f * s)

        fun project(x: Float, y: Float, z: Float): Offset = Offset(
            origin.x + x * ex.x + z * ez.x + y * ey.x,
            origin.y + x * ex.y + z * ez.y + y * ey.y
        )

        fun stickerColor(ch: Char): Color = when (ch) {
            'y' -> Color(0xFFFFD600)
            'r' -> Color(0xFFFF5252)
            'g' -> Color(0xFF00E676)
            'w' -> Color(0xFFE0E0E0)
            'o' -> Color(0xFFFF9800)
            'b' -> Color(0xFF2196F3)
            else -> Color(0xFF262C3A)
        }

        // world(row,col) gives the 3D center of the sticker at the given face grid
        // cell. Facelet rows are bottom-first for side faces, so callers flip rows.
        fun drawFace(face: String, world: (row: Int, col: Int) -> Triple<Float, Float, Float>) {
            for (row in 0..2) {
                for (col in 0..2) {
                    val (wx, wy, wz) = world(row, col)
                    val center = project(wx, wy, wz)
                    val half = 0.115f * s
                    val path = Path().apply {
                        moveTo(center.x, center.y - half)
                        lineTo(center.x + half, center.y)
                        lineTo(center.x, center.y + half)
                        lineTo(center.x - half, center.y)
                        close()
                    }
                    drawPath(path, stickerColor(face[row * 3 + col]))
                    drawPath(path, Color(0xFF10141E), style = Stroke(width = 1.2.dp.toPx()))
                }
            }
        }

        // U face (top): row0 = back (z=-1) .. row2 = front (z=+1); cols x=-1..1
        drawFace(uFace) { row, col ->
            Triple((col - 1).toFloat(), 1f, (row - 1).toFloat())
        }

        // F face (front): facelet row0 = D-side (y=-1) .. row2 = U-side (y=+1)
        drawFace(fFace) { row, col ->
            Triple((col - 1).toFloat(), (row - 1).toFloat(), 1f)
        }

        // R face (right): facelet row0 = D-side .. row2 = U-side; col0 = front (z=+1)
        drawFace(rFace) { row, col ->
            Triple(1f, (row - 1).toFloat(), (1 - col).toFloat())
        }

        // Highlight the FR slot target
        drawCircle(
            color = slotGlow,
            radius = 5.dp.toPx(),
            center = project(0.66f, 0.35f, 1f)
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
