package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmCase
import com.example.model.CubeStage
import com.example.model.PlaybackStyle
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CFOPDrillScreen(
    viewModel: CFOPDrillViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val allCases by viewModel.allCases.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var showCopiedSnackbar by remember { mutableStateOf(false) }

    // Filter cases by category, subcategory, and search query
    val filteredCases = remember(allCases, uiState.selectedStage, uiState.selectedSubCategory, uiState.searchQuery) {
        allCases.filter { case ->
            val matchesStage = when (uiState.selectedStage) {
                CubeStage.CUSTOM -> case.isCustom
                else -> case.category == uiState.selectedStage
            }
            val matchesSub = uiState.selectedSubCategory == null || case.subCategory == uiState.selectedSubCategory
            val matchesSearch = uiState.searchQuery.isBlank() ||
                    case.name.contains(uiState.searchQuery, ignoreCase = true) ||
                    case.algorithm.contains(uiState.searchQuery, ignoreCase = true) ||
                    case.description.contains(uiState.searchQuery, ignoreCase = true)

            matchesStage && matchesSub && matchesSearch
        }
    }

    // Subcategories for current stage
    val availableSubCategories = remember(allCases, uiState.selectedStage) {
        allCases.filter { it.category == uiState.selectedStage }
            .map { it.subCategory }
            .distinct()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CFOP Drill",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Audio Algorithm Trainer",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Add Custom Algorithm
                    IconButton(
                        onClick = { viewModel.setShowAddDialog(true) },
                        modifier = Modifier.testTag("add_custom_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Custom Algorithm",
                            tint = Color(0xFF00E5FF)
                        )
                    }

                    // Audio Settings
                    IconButton(
                        onClick = { viewModel.setShowSettingsSheet(true) },
                        modifier = Modifier.testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Audio Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F131D)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .testTag("drill_screen_content")
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stage Selection Tabs (F2L, OLL, PLL, Custom)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .testTag("stage_selector_row")
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF131722))
                        .border(1.dp, Color(0xFF263047), RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(CubeStage.F2L, CubeStage.OLL, CubeStage.PLL, CubeStage.CUSTOM).forEach { stage ->
                        val isSelected = uiState.selectedStage == stage
                        val count = allCases.count { if (stage == CubeStage.CUSTOM) it.isCustom else it.category == stage }

                        Box(
                            modifier = Modifier
                                .testTag("stage_tab_${stage.name}")
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF00E5FF) else Color.Transparent)
                                .clickable { viewModel.selectStage(stage) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stage.badge,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF08121E) else Color(0xFFCBD5E1),
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color(0xFF08121E).copy(alpha = 0.7f) else Color(0xFF64748B),
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Subcategory Filters if applicable
            if (availableSubCategories.isNotEmpty()) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedSubCategory == null,
                                onClick = { viewModel.selectSubCategory(null) },
                                label = { Text("All", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFF00E5FF),
                                    containerColor = Color(0xFF131722),
                                    labelColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                        items(availableSubCategories) { sub ->
                            val selected = uiState.selectedSubCategory == sub
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.selectSubCategory(if (selected) null else sub) },
                                label = { Text(sub, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFF00E5FF),
                                    containerColor = Color(0xFF131722),
                                    labelColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }

            // Active Algorithm Workbench Card
            uiState.currentCase?.let { currentCase ->
                item {
                    Column(
                        modifier = Modifier
                            .testTag("active_case_card")
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF131722))
                            .border(1.dp, Color(0xFF263047), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        // Header: Name, Category badge, Copy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentCase.name,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = currentCase.subCategory,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF00E5FF),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                                if (currentCase.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentCase.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Copy Algorithm button
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(currentCase.algorithm))
                                    showCopiedSnackbar = true
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Algorithm",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Center: Cube Visualizer & Full Notation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CubeVisualizer(
                                case = currentCase,
                                isPlaying = uiState.isPlaying,
                                modifier = Modifier.size(110.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ALGORITHM NOTATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentCase.algorithm,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 16.sp,
                                        lineHeight = 22.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${uiState.currentSteps.size} moves • ${if (uiState.audioSettings.playbackStyle == PlaybackStyle.CONTINUOUS) "Fluid Flow" else "Paced Metronome"}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF00E5FF),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Move Tape (Audio synchronized)
            if (uiState.currentSteps.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF131722))
                            .border(1.dp, Color(0xFF263047), RoundedCornerShape(16.dp))
                            .padding(vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Audio Step Drill Tape",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(0xFFCBD5E1),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            val currentMove = uiState.currentSteps.getOrNull(uiState.activeStepIndex)
                            if (currentMove != null) {
                                Text(
                                    text = "Speaking: ${currentMove.spoken}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF00E5FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            } else {
                                Text(
                                    text = "Tap step to jump",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF64748B)
                                    )
                                )
                            }
                        }

                        MoveTape(
                            steps = uiState.currentSteps,
                            activeStepIndex = uiState.activeStepIndex,
                            onStepClick = { index ->
                                viewModel.jumpToStep(index)
                            }
                        )
                    }
                }
            }

            // Playback Controls (Play/Pause, Paced/Fluid, Speed slider)
            item {
                PlaybackControls(
                    isPlaying = uiState.isPlaying,
                    settings = uiState.audioSettings,
                    onPlayPauseToggle = { viewModel.togglePlayPause() },
                    onRestart = { viewModel.restartFromBeginning() },
                    onPreviousStep = { viewModel.stepPrevious() },
                    onNextStep = { viewModel.stepNext() },
                    onSpeedChange = { viewModel.setSpeed(it) },
                    onPlaybackStyleChange = { viewModel.setPlaybackStyle(it) },
                    onSettingsClick = { viewModel.setShowSettingsSheet(true) }
                )
            }

            // Case Selection Drawer / List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.selectedStage.displayName} Cases (${filteredCases.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    // Search text input
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Filter cases...", fontSize = 12.sp) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF263047),
                            focusedContainerColor = Color(0xFF131722),
                            unfocusedContainerColor = Color(0xFF131722)
                        ),
                        modifier = Modifier
                            .testTag("search_cases_input")
                            .width(180.dp)
                            .height(48.dp)
                    )
                }
            }

            // Grid or list of available cases
            items(filteredCases) { case ->
                val isSelected = uiState.currentCase?.id == case.id
                Box(
                    modifier = Modifier
                        .testTag("case_item_${case.id}")
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFF1A2337) else Color(0xFF131722))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF263047),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.selectCase(case) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = case.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF00E5FF) else Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${case.subCategory}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = case.algorithm,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp
                                ),
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = if (isSelected && uiState.isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayCircleOutline,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF00E5FF) else Color(0xFF64748B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Settings Bottom Sheet
    if (uiState.showSettingsSheet) {
        AudioSettingsBottomSheet(
            settings = uiState.audioSettings,
            onSettingsChange = { newSettings ->
                viewModel.updateAudioSettings(newSettings)
            },
            onTestMetronome = {
                viewModel.testMetronomeTick()
            },
            onDismiss = {
                viewModel.setShowSettingsSheet(false)
            }
        )
    }

    // Add Custom Algorithm Dialog
    if (uiState.showAddDialog) {
        AddCustomAlgorithmDialog(
            initialStage = uiState.selectedStage,
            onDismiss = { viewModel.setShowAddDialog(false) },
            onSave = { newCase ->
                viewModel.addCustomAlgorithm(newCase)
            }
        )
    }
}
