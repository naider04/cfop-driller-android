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
import kotlin.math.atan2
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

        // Draw permutation swap lines/arrows with clear directional arrowheads
        arrows.forEach { (from, to) ->
            val p1 = getPerimeterCenter(from)
            val p2 = getPerimeterCenter(to)

            // Line body
            drawLine(
                color = primaryCyan,
                start = p1,
                end = p2,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Origin dot
            drawCircle(
                color = Color.White,
                radius = 3.5.dp.toPx(),
                center = p1
            )

            // Directional Arrowhead pointing from p1 towards p2
            val angle = atan2(p2.y - p1.y, p2.x - p1.x)
            val arrowLength = 9.dp.toPx()
            val arrowAngle = Math.toRadians(32.0).toFloat()

            val leftWing = Offset(
                x = p2.x - arrowLength * cos(angle - arrowAngle),
                y = p2.y - arrowLength * sin(angle - arrowAngle)
            )
            val rightWing = Offset(
                x = p2.x - arrowLength * cos(angle + arrowAngle),
                y = p2.y - arrowLength * sin(angle + arrowAngle)
            )

            val arrowHeadPath = Path().apply {
                moveTo(p2.x, p2.y)
                lineTo(leftWing.x, leftWing.y)
                lineTo(rightWing.x, rightWing.y)
                close()
            }
            drawPath(
                path = arrowHeadPath,
                color = primaryCyan
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

        // Dimetric/Isometric basis vectors:
        // u = along right face top edge (+30 deg from horiz): (cos 30°, -sin 30°)
        // v = along front face top edge (+150 deg from horiz): (-cos 30°, -sin 30°)
        // w = vertical downward (+90 deg): (0, 1)
        val cos30 = 0.8660254f
        val sin30 = 0.5000000f
        val unit = 0.165f * s

        val uVec = Offset(cos30 * unit, -sin30 * unit)
        val vVec = Offset(-cos30 * unit, -sin30 * unit)
        val wVec = Offset(0f, unit)

        // Center apex (top-front corner of the cube)
        val apex = Offset(size.width * 0.50f, size.height * 0.44f)

        fun stickerColor(ch: Char): Color = when (ch) {
            'y' -> Color(0xFFFFD600)
            'r' -> Color(0xFFFF3D00)
            'g' -> Color(0xFF00E676)
            'w' -> Color(0xFFF0F4F8)
            'o' -> Color(0xFFFF9100)
            'b' -> Color(0xFF2979FF)
            else -> Color(0xFF1E2433)
        }

        // 1. Draw Up (U) face: parallel to (vVec, uVec)
        for (row in 0..2) {
            for (col in 0..2) {
                val color = stickerColor(uFace[row * 3 + col])
                val c0 = apex + vVec * (3 - row).toFloat() + uVec * col.toFloat()
                val c1 = apex + vVec * (3 - row).toFloat() + uVec * (col + 1).toFloat()
                val c2 = apex + vVec * (2 - row).toFloat() + uVec * (col + 1).toFloat()
                val c3 = apex + vVec * (2 - row).toFloat() + uVec * col.toFloat()

                val path = Path().apply {
                    moveTo(c0.x, c0.y)
                    lineTo(c1.x, c1.y)
                    lineTo(c2.x, c2.y)
                    lineTo(c3.x, c3.y)
                    close()
                }
                drawPath(path, color)
                drawPath(path, Color(0xFF10141E), style = Stroke(width = 1.2.dp.toPx()))
            }
        }

        // 2. Draw Front (F) face: parallel to (vVec, wVec)
        for (layer in 0..2) {
            for (col in 0..2) {
                val rowIdx = 2 - layer // row 2 is top layer (U side), row 0 is D side
                val color = stickerColor(fFace[rowIdx * 3 + col])
                val vOffset = 2 - col

                val c0 = apex + vVec * vOffset.toFloat() + wVec * layer.toFloat()
                val c1 = apex + vVec * (vOffset + 1).toFloat() + wVec * layer.toFloat()
                val c2 = apex + vVec * (vOffset + 1).toFloat() + wVec * (layer + 1).toFloat()
                val c3 = apex + vVec * vOffset.toFloat() + wVec * (layer + 1).toFloat()

                val path = Path().apply {
                    moveTo(c0.x, c0.y)
                    lineTo(c1.x, c1.y)
                    lineTo(c2.x, c2.y)
                    lineTo(c3.x, c3.y)
                    close()
                }
                drawPath(path, color)
                drawPath(path, Color(0xFF10141E), style = Stroke(width = 1.2.dp.toPx()))
            }
        }

        // 3. Draw Right (R) face: parallel to (uVec, wVec)
        for (layer in 0..2) {
            for (col in 0..2) {
                val rowIdx = 2 - layer
                val color = stickerColor(rFace[rowIdx * 3 + col])

                val c0 = apex + uVec * col.toFloat() + wVec * layer.toFloat()
                val c1 = apex + uVec * (col + 1).toFloat() + wVec * layer.toFloat()
                val c2 = apex + uVec * (col + 1).toFloat() + wVec * (layer + 1).toFloat()
                val c3 = apex + uVec * col.toFloat() + wVec * (layer + 1).toFloat()

                val path = Path().apply {
                    moveTo(c0.x, c0.y)
                    lineTo(c1.x, c1.y)
                    lineTo(c2.x, c2.y)
                    lineTo(c3.x, c3.y)
                    close()
                }
                drawPath(path, color)
                drawPath(path, Color(0xFF10141E), style = Stroke(width = 1.2.dp.toPx()))
            }
        }

        // Highlight the Front-Right slot target (layer 1 & 2 at FR corner)
        val slotTarget = apex + wVec * 1.5f
        drawCircle(
            color = slotGlow.copy(alpha = 0.8f),
            radius = 4.dp.toPx(),
            center = slotTarget
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
