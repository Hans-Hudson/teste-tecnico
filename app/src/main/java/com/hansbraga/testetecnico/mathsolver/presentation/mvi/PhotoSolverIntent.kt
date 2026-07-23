package com.hansbraga.testetecnico.mathsolver.presentation.mvi

sealed interface PhotoSolverIntent {
    data class ImageCaptured(val imageBytes: ByteArray) : PhotoSolverIntent
    data object Reset : PhotoSolverIntent
}
