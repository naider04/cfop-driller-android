package com.example.model

enum class PlaybackStyle(val label: String) {
    CONTINUOUS("Fluid"),
    PACED("Paced")
}

enum class PronunciationMode(val label: String) {
    NOTATION("Notation Only"),
    DESCRIPTIVE("With Finger Tricks")
}

data class AudioSettings(
    val speed: Float = 1.1f,
    val moveDelayMs: Long = 450L,
    val pitch: Float = 1.0f,
    val volume: Float = 1.0f,
    val pronunciationMode: PronunciationMode = PronunciationMode.NOTATION,
    val playbackStyle: PlaybackStyle = PlaybackStyle.CONTINUOUS,
    val metronomeTick: Boolean = true,
    val loopCountdown: Boolean = true,
    val loop: Boolean = true
)

enum class TurnType {
    FACE,
    WIDE,
    ROTATION,
    SLICE
}

data class MoveStep(
    val raw: String,
    val spoken: String,
    val fingerHint: String,
    val turnType: TurnType = TurnType.FACE,
    val triggerGroup: String? = null
)

enum class CubeStage(val displayName: String, val badge: String) {
    F2L("First 2 Layers", "F2L"),
    OLL("Orientation (OLL)", "OLL"),
    PLL("Permutation (PLL)", "PLL"),
    CUSTOM("Custom", "Custom")
}

enum class DiagramType {
    OLL_TOP,
    PLL_CYCLE,
    F2L_SLOT,
    GENERIC_CUBE
}

data class AlgorithmCase(
    val id: String,
    val name: String,
    val category: CubeStage,
    val subCategory: String,
    val algorithm: String,
    val description: String = "",
    val diagramType: DiagramType = DiagramType.GENERIC_CUBE,
    val yellowPattern: List<Boolean> = emptyList(), // 9 booleans for 3x3 OLL top face
    val pllArrows: List<Pair<Int, Int>> = emptyList(), // Pairs of indices (0..7 around perimeter) for swaps
    val isCustom: Boolean = false
)
