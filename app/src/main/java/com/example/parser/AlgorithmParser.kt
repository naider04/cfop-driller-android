package com.example.parser

import com.example.model.MoveStep
import com.example.model.TurnType

object AlgorithmParser {

    /**
     * Parses standard speedcubing algorithm notation into individual spoken steps
     * with phonetics for Text-to-Speech and finger trick instructions.
     */
    fun parseAlgorithmMoves(algorithmStr: String): List<MoveStep> {
        // Strip out brackets and simplify whitespace
        val cleaned = algorithmStr
            .replace("[", " ")
            .replace("]", " ")
            .replace("(", " ")
            .replace(")", " ")
            .trim()

        if (cleaned.isBlank()) return emptyList()

        // Match tokens like: R, R', R2, R2', Rw, Rw', Rw2, r, r', r2, M, M', M2, x, x', x2, etc.
        val tokens = cleaned.split("\\s+".toRegex()).filter { it.isNotBlank() }

        return tokens.map { token ->
            parseSingleMove(token)
        }
    }

    private fun parseSingleMove(rawToken: String): MoveStep {
        // Normalize token
        val token = rawToken.trim()
        val isDouble = token.contains("2")
        val isPrime = token.contains("'") || token.contains("’")

        // Check base letter and variations
        val baseLetter = token.replace("2", "").replace("'", "").replace("’", "")

        val turnType: TurnType
        val spokenBase: String
        val fingerHint: String

        when (baseLetter) {
            "R" -> {
                turnType = TurnType.FACE
                spokenBase = "R"
                fingerHint = when {
                    isDouble -> "right wrist double flick"
                    isPrime -> "right wrist down"
                    else -> "right wrist up"
                }
            }
            "L" -> {
                turnType = TurnType.FACE
                spokenBase = "L"
                fingerHint = when {
                    isDouble -> "left wrist double flick"
                    isPrime -> "left wrist up"
                    else -> "left wrist down"
                }
            }
            "U" -> {
                turnType = TurnType.FACE
                spokenBase = "U"
                fingerHint = when {
                    isDouble -> "index middle double flick"
                    isPrime -> "left index pull"
                    else -> "right index pull"
                }
            }
            "D" -> {
                turnType = TurnType.FACE
                spokenBase = "D"
                fingerHint = when {
                    isDouble -> "ring pinky double flick"
                    isPrime -> "right ring push"
                    else -> "left ring push"
                }
            }
            "F" -> {
                turnType = TurnType.FACE
                spokenBase = "F"
                fingerHint = when {
                    isDouble -> "double push front"
                    isPrime -> "right thumb push up"
                    else -> "right index push down"
                }
            }
            "B" -> {
                turnType = TurnType.FACE
                spokenBase = "B"
                fingerHint = when {
                    isDouble -> "double turn back"
                    isPrime -> "right index push back"
                    else -> "right ring pull back"
                }
            }
            "r", "Rw" -> {
                turnType = TurnType.WIDE
                spokenBase = "wide R"
                fingerHint = when {
                    isDouble -> "wide right double turn"
                    isPrime -> "wide right down"
                    else -> "wide right up"
                }
            }
            "l", "Lw" -> {
                turnType = TurnType.WIDE
                spokenBase = "wide L"
                fingerHint = when {
                    isDouble -> "wide left double turn"
                    isPrime -> "wide left up"
                    else -> "wide left down"
                }
            }
            "u", "Uw" -> {
                turnType = TurnType.WIDE
                spokenBase = "wide U"
                fingerHint = when {
                    isDouble -> "wide double flick upper"
                    isPrime -> "wide left index pull"
                    else -> "wide right index pull"
                }
            }
            "d", "Dw" -> {
                turnType = TurnType.WIDE
                spokenBase = "wide D"
                fingerHint = when {
                    isDouble -> "wide double bottom"
                    isPrime -> "wide right ring push"
                    else -> "wide left ring push"
                }
            }
            "f", "Fw" -> {
                turnType = TurnType.WIDE
                spokenBase = "wide F"
                fingerHint = when {
                    isDouble -> "wide double front"
                    isPrime -> "wide right thumb up"
                    else -> "wide right index push"
                }
            }
            "b", "Bw" -> {
                turnType = TurnType.WIDE
                spokenBase = "wide B"
                fingerHint = when {
                    isDouble -> "wide double back"
                    isPrime -> "wide right index back"
                    else -> "wide right ring back"
                }
            }
            "M" -> {
                turnType = TurnType.SLICE
                spokenBase = "M"
                fingerHint = when {
                    isDouble -> "ring middle double flick slice"
                    isPrime -> "ring finger slice up"
                    else -> "index push slice down"
                }
            }
            "S" -> {
                turnType = TurnType.SLICE
                spokenBase = "S"
                fingerHint = if (isPrime) "standing slice counter" else "standing slice clockwise"
            }
            "E" -> {
                turnType = TurnType.SLICE
                spokenBase = "E"
                fingerHint = if (isPrime) "equatorial slice right" else "equatorial slice left"
            }
            "x" -> {
                turnType = TurnType.ROTATION
                spokenBase = "x"
                fingerHint = when {
                    isDouble -> "flip cube forward"
                    isPrime -> "tilt cube back"
                    else -> "tilt cube forward"
                }
            }
            "y" -> {
                turnType = TurnType.ROTATION
                spokenBase = "y"
                fingerHint = when {
                    isDouble -> "rotate cube 180"
                    isPrime -> "rotate cube left"
                    else -> "rotate cube right"
                }
            }
            "z" -> {
                turnType = TurnType.ROTATION
                spokenBase = "z"
                fingerHint = when {
                    isDouble -> "tilt cube 180 roll"
                    isPrime -> "tilt cube counter clockwise"
                    else -> "tilt cube clockwise"
                }
            }
            else -> {
                turnType = TurnType.FACE
                spokenBase = baseLetter
                fingerHint = "turn $token"
            }
        }

        // Construct natural spoken word
        val spoken = when {
            isDouble && isPrime -> "$spokenBase two"
            isDouble -> "$spokenBase two"
            isPrime -> "$spokenBase prime"
            else -> spokenBase
        }

        return MoveStep(
            raw = token,
            spoken = spoken,
            fingerHint = fingerHint,
            turnType = turnType
        )
    }
}
