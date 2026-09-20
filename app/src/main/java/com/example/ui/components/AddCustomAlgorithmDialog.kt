package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmCase
import com.example.model.CubeStage
import com.example.model.DiagramType
import java.util.UUID

@Composable
fun AddCustomAlgorithmDialog(
    initialStage: CubeStage,
    onDismiss: () -> Unit,
    onSave: (AlgorithmCase) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var notation by remember { mutableStateOf("") }
    var selectedStage by remember { mutableStateOf(if (initialStage == CubeStage.CUSTOM) CubeStage.PLL else initialStage) }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131722),
        title = {
            Text(
                text = "Add Custom Algorithm",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Case Name (e.g. Back Sune, V-Perm alt)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF263047)
                    ),
                    modifier = Modifier
                        .testTag("custom_algo_name_input")
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = notation,
                    onValueChange = { notation = it },
                    label = { Text("Algorithm Notation (e.g. R U R' U')") },
                    placeholder = { Text("r U R' U' r' F R F'") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF263047)
                    ),
                    modifier = Modifier
                        .testTag("custom_algo_moves_input")
                        .fillMaxWidth()
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF94A3B8))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(CubeStage.F2L, CubeStage.OLL, CubeStage.PLL).forEach { stage ->
                        val selected = selectedStage == stage
                        FilterChip(
                            selected = selected,
                            onClick = { selectedStage = stage },
                            label = { Text(stage.badge, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00E5FF),
                                selectedLabelColor = Color(0xFF09111E),
                                containerColor = Color(0xFF1E2638),
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes / Fingertricks (Optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF263047)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && notation.isNotBlank()) {
                        val newCase = AlgorithmCase(
                            id = "custom_${UUID.randomUUID()}",
                            name = name.trim(),
                            category = selectedStage,
                            subCategory = "Custom",
                            algorithm = notation.trim(),
                            description = description.trim(),
                            diagramType = when (selectedStage) {
                                CubeStage.OLL -> DiagramType.OLL_TOP
                                CubeStage.PLL -> DiagramType.PLL_CYCLE
                                CubeStage.F2L -> DiagramType.F2L_SLOT
                                else -> DiagramType.GENERIC_CUBE
                            },
                            isCustom = true
                        )
                        onSave(newCase)
                    }
                },
                enabled = name.isNotBlank() && notation.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF09111E)
                ),
                modifier = Modifier.testTag("save_custom_algo_button")
            ) {
                Text("Add Algorithm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}
