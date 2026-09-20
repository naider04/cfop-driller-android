package com.example.data

import com.example.model.AlgorithmCase
import com.example.model.CubeStage
import com.example.model.DiagramType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AlgorithmRepository {

    private val defaultCases = listOf(
        // ================= PLL =================
        AlgorithmCase(
            id = "pll_t",
            name = "T-Permutation",
            category = CubeStage.PLL,
            subCategory = "Adjacent Swap",
            algorithm = "R U R' U' R' F R2 U' R' U' R U R' F'",
            description = "Swaps front-right and back-right corners, and right & front edges.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(2, 4), Pair(1, 3))
        ),
        AlgorithmCase(
            id = "pll_jb",
            name = "Jb-Permutation",
            category = CubeStage.PLL,
            subCategory = "Adjacent Swap",
            algorithm = "R U R' F' R U R' U' R' F R2 U' R'",
            description = "Swaps front-right and front-left corners with left & front edges.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(6, 4), Pair(5, 3))
        ),
        AlgorithmCase(
            id = "pll_ja",
            name = "Ja-Permutation",
            category = CubeStage.PLL,
            subCategory = "Adjacent Swap",
            algorithm = "x R2 F R F' R U2 r' U r U2 x'",
            description = "Mirror of Jb; swaps back-left and back-right corners.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(0, 2), Pair(1, 7))
        ),
        AlgorithmCase(
            id = "pll_y",
            name = "Y-Permutation",
            category = CubeStage.PLL,
            subCategory = "Diagonal Swap",
            algorithm = "F R U' R' U' R U R' F' R U R' U' R' F R F'",
            description = "Diagonal corner swap: back-left and front-right corners swap.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(0, 4), Pair(1, 7))
        ),
        AlgorithmCase(
            id = "pll_h",
            name = "H-Permutation",
            category = CubeStage.PLL,
            subCategory = "Edges Only",
            algorithm = "M2 U M2 U2 M2 U M2",
            description = "Swaps opposite edges: Front with Back, Left with Right.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(1, 5), Pair(3, 7))
        ),
        AlgorithmCase(
            id = "pll_ua",
            name = "Ua-Permutation",
            category = CubeStage.PLL,
            subCategory = "Edges Only",
            algorithm = "M2 U M U2 M' U M2",
            description = "3-edge counter-clockwise cycle: Front -> Right -> Left -> Front.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(3, 5), Pair(5, 7), Pair(7, 3))
        ),
        AlgorithmCase(
            id = "pll_ub",
            name = "Ub-Permutation",
            category = CubeStage.PLL,
            subCategory = "Edges Only",
            algorithm = "M2 U' M U2 M' U' M2",
            description = "3-edge clockwise cycle: Front -> Left -> Right -> Front.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(3, 7), Pair(7, 5), Pair(5, 3))
        ),
        AlgorithmCase(
            id = "pll_z",
            name = "Z-Permutation",
            category = CubeStage.PLL,
            subCategory = "Edges Only",
            algorithm = "M' U M2 U M2 U M' U2 M2",
            description = "Swaps adjacent edge pairs (Front-Right and Back-Left).",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(3, 5), Pair(1, 7))
        ),
        AlgorithmCase(
            id = "pll_aa",
            name = "Aa-Permutation",
            category = CubeStage.PLL,
            subCategory = "Corners Only",
            algorithm = "x R' U R' D2 R U' R' D2 R2 x'",
            description = "3-corner cycle: back-left, front-right, front-left.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(0, 4), Pair(4, 6))
        ),
        AlgorithmCase(
            id = "pll_ab",
            name = "Ab-Permutation",
            category = CubeStage.PLL,
            subCategory = "Corners Only",
            algorithm = "x R2 D2 R U R' D2 R U' R x'",
            description = "Counter-clockwise 3-corner cycle.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(0, 6), Pair(6, 4))
        ),
        AlgorithmCase(
            id = "pll_e",
            name = "E-Permutation",
            category = CubeStage.PLL,
            subCategory = "Diagonal Swap",
            algorithm = "x' R U' R' D R U R' D' R U R' D R U' R' D' x",
            description = "Diagonal swap of both front and back corner pairs.",
            diagramType = DiagramType.PLL_CYCLE,
            pllArrows = listOf(Pair(0, 2), Pair(6, 4))
        ),
        AlgorithmCase(
            id = "pll_ra",
            name = "Ra-Permutation",
            category = CubeStage.PLL,
            subCategory = "Adjacent Swap",
            algorithm = "R U R' F' R U2 R' U2 R' F R U R U2 R'",
            description = "Front-right headlights with adjacent swaps.",
            diagramType = DiagramType.PLL_CYCLE
        ),
        AlgorithmCase(
            id = "pll_rb",
            name = "Rb-Permutation",
            category = CubeStage.PLL,
            subCategory = "Adjacent Swap",
            algorithm = "R' U2 R U2 R' F R U R' U' R' F' R2 U'",
            description = "Back-right headlights with adjacent swaps.",
            diagramType = DiagramType.PLL_CYCLE
        ),
        AlgorithmCase(
            id = "pll_f",
            name = "F-Permutation",
            category = CubeStage.PLL,
            subCategory = "Adjacent Swap",
            algorithm = "R' U' F' R U R' U' R' F R2 U' R' U' R U R' U R",
            description = "T-perm variation with setup move.",
            diagramType = DiagramType.PLL_CYCLE
        ),
        AlgorithmCase(
            id = "pll_v",
            name = "V-Permutation",
            category = CubeStage.PLL,
            subCategory = "Diagonal Swap",
            algorithm = "R' U R' U' y R' F' R2 U' R' U R' F R F",
            description = "Diagonal corner swap with rotation trigger.",
            diagramType = DiagramType.PLL_CYCLE
        ),

        // ================= OLL =================
        AlgorithmCase(
            id = "oll_sune",
            name = "Sune (OLL 27)",
            category = CubeStage.OLL,
            subCategory = "2-Look Corners",
            algorithm = "R U R' U R U2 R'",
            description = "Single oriented corner at front-left; yellow cross solved.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, true, false,
                true,  true, true,
                true,  true, false
            )
        ),
        AlgorithmCase(
            id = "oll_antisune",
            name = "Anti-Sune (OLL 26)",
            category = CubeStage.OLL,
            subCategory = "2-Look Corners",
            algorithm = "R U2 R' U' R U' R'",
            description = "Single oriented corner at back-right; mirror of Sune.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, true, true,
                true,  true, true,
                false, true, false
            )
        ),
        AlgorithmCase(
            id = "oll_h",
            name = "H / Double Sune (OLL 21)",
            category = CubeStage.OLL,
            subCategory = "2-Look Corners",
            algorithm = "R U2 R' U' R U R' U' R U' R'",
            description = "Yellow cross solved; headlights on both left and right sides.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, true, false,
                true,  true, true,
                false, true, false
            )
        ),
        AlgorithmCase(
            id = "oll_pi",
            name = "Pi / Headlights (OLL 22)",
            category = CubeStage.OLL,
            subCategory = "2-Look Corners",
            algorithm = "R U2 R2 U' R2 U' R2 U2 R",
            description = "Yellow cross solved; headlights front, corners facing outward back.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, true, false,
                true,  true, true,
                false, true, false
            )
        ),
        AlgorithmCase(
            id = "oll_headlights",
            name = "Headlights / U (OLL 23)",
            category = CubeStage.OLL,
            subCategory = "2-Look Corners",
            algorithm = "R2 D R' U2 R D' R' U2 R'",
            description = "Two solved corners in back, headlights in front.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                true,  true, true,
                true,  true, true,
                false, true, false
            )
        ),
        AlgorithmCase(
            id = "oll_chameleon",
            name = "Chameleon / T (OLL 24)",
            category = CubeStage.OLL,
            subCategory = "2-Look Corners",
            algorithm = "r U R' U' r' F R F'",
            description = "Wide move trigger; two corners solved diagonally.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                true,  true, false,
                true,  true, true,
                true,  true, false
            )
        ),
        AlgorithmCase(
            id = "oll_bowtie",
            name = "Bowtie / L (OLL 25)",
            category = CubeStage.OLL,
            subCategory = "2-Look Corners",
            algorithm = "F' r U R' U' r' F R",
            description = "Two opposite corners oriented; diagonal headlights.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, true, true,
                true,  true, true,
                true,  true, false
            )
        ),
        AlgorithmCase(
            id = "oll_fruruf",
            name = "FRUR'U'F' Line (OLL 45)",
            category = CubeStage.OLL,
            subCategory = "2-Look Edges",
            algorithm = "F R U R' U' F'",
            description = "Horizontal yellow line; creates yellow cross.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, false, false,
                true,  true,  true,
                false, false, false
            )
        ),
        AlgorithmCase(
            id = "oll_small_l",
            name = "Small L (OLL 44)",
            category = CubeStage.OLL,
            subCategory = "2-Look Edges",
            algorithm = "f R U R' U' f'",
            description = "Wide front move on top-left L-shape to form yellow cross.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, true,  false,
                true,  true,  false,
                false, false, false
            )
        ),
        AlgorithmCase(
            id = "oll_checkers",
            name = "Checkers (OLL 28)",
            category = CubeStage.OLL,
            subCategory = "Corners Solved",
            algorithm = "r U R' U' M U R U' R'",
            description = "All 4 corners yellow, 2 opposite edges flipped.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                true,  false, true,
                true,  true,  true,
                true,  false, true
            )
        ),
        AlgorithmCase(
            id = "oll_fish",
            name = "Fish Shape (OLL 33)",
            category = CubeStage.OLL,
            subCategory = "T-Shapes",
            algorithm = "R U R' U' R' F R F'",
            description = "Sexy move into sledgehammer trigger.",
            diagramType = DiagramType.OLL_TOP,
            yellowPattern = listOf(
                false, true, false,
                true,  true, true,
                false, false, true
            )
        ),

        // ================= F2L =================
        AlgorithmCase(
            id = "f2l_sexy_insert",
            name = "Right Sexy Insert",
            category = CubeStage.F2L,
            subCategory = "Basic Inserts",
            algorithm = "U R U' R'",
            description = "Standard insertion of paired corner-edge into Front-Right slot.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_left_mirror",
            name = "Left Mirror Insert",
            category = CubeStage.F2L,
            subCategory = "Basic Inserts",
            algorithm = "U' L' U L",
            description = "Mirror insertion into Front-Left slot.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_direct",
            name = "Direct Slot Insert",
            category = CubeStage.F2L,
            subCategory = "Basic Inserts",
            algorithm = "R U' R'",
            description = "Immediate 3-move insert into Front-Right slot.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_sledgehammer",
            name = "Sledgehammer Insert",
            category = CubeStage.F2L,
            subCategory = "Special Triggers",
            algorithm = "R' F R F'",
            description = "Orienting edge insert that controls top cross edges.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_sexy_pair",
            name = "Sexy Move Trigger",
            category = CubeStage.F2L,
            subCategory = "Special Triggers",
            algorithm = "R U R' U'",
            description = "The foundational cubing trigger for pairing and cycling.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_connected_easy",
            name = "Corner & Edge Paired in U",
            category = CubeStage.F2L,
            subCategory = "Free Pairs",
            algorithm = "U' R U R' U2 R U' R'",
            description = "Setting up orientation and inserting cleanly into front-right.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_split_pair",
            name = "Split Pair Opposite Colors",
            category = CubeStage.F2L,
            subCategory = "Separation",
            algorithm = "d R' U' R U2 R' U R",
            description = "Using wide D rotation to pair up and slot in.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_white_up",
            name = "Corner White Facing Up",
            category = CubeStage.F2L,
            subCategory = "Corner Up",
            algorithm = "R U2 R' U' R U R'",
            description = "Rotate white sticker from top face to side, then insert.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_corner_in_slot",
            name = "Corner in Slot, Edge in U",
            category = CubeStage.F2L,
            subCategory = "Slot Extraction",
            algorithm = "R U' R' U R U' R'",
            description = "Eject corner from slot while pairing with incoming edge.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_edge_in_slot",
            name = "Edge in Slot, Corner in U",
            category = CubeStage.F2L,
            subCategory = "Slot Extraction",
            algorithm = "U R U' R' U' R U R'",
            description = "Eject edge while maintaining corner position for 3-move insert.",
            diagramType = DiagramType.F2L_SLOT
        ),
        AlgorithmCase(
            id = "f2l_back_slot",
            name = "Back Right Slot Insert",
            category = CubeStage.F2L,
            subCategory = "Back Slots",
            algorithm = "R' U' R U R' U' R",
            description = "Ergonomic insertion into back-right slot without rotation.",
            diagramType = DiagramType.F2L_SLOT
        )
    )

    private val _cases = MutableStateFlow(defaultCases)
    val cases: StateFlow<List<AlgorithmCase>> = _cases.asStateFlow()

    fun addCustomAlgorithm(case: AlgorithmCase) {
        _cases.update { listOf(case) + it }
    }

    fun removeCustomAlgorithm(id: String) {
        _cases.update { current -> current.filterNot { it.id == id } }
    }
}
