package com.hansbraga.testetecnico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hansbraga.testetecnico.calculator.presentation.ui.CalculatorScreen
import com.hansbraga.testetecnico.core.theme.TesteTecnicoTheme
import com.hansbraga.testetecnico.mathsolver.presentation.ui.PhotoSolverScreen

private enum class AppScreen { Calculator, PhotoSolver }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TesteTecnicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Calculator) }
                    when (currentScreen) {
                        AppScreen.Calculator -> CalculatorScreen(
                            onOpenPhotoSolver = { currentScreen = AppScreen.PhotoSolver }
                        )

                        AppScreen.PhotoSolver -> PhotoSolverScreen(
                            onNavigateBack = { currentScreen = AppScreen.Calculator }
                        )
                    }
                }
            }
        }
    }
}
