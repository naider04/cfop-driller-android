package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CFOPDrillScreen
import com.example.ui.CFOPDrillViewModel
import com.example.ui.theme.CFOPDrillTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CFOPDrillTheme {
                val viewModel: CFOPDrillViewModel = viewModel()
                CFOPDrillScreen(viewModel = viewModel)
            }
        }
    }
}
