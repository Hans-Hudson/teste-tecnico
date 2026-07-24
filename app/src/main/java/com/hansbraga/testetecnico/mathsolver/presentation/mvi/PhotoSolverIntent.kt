package com.hansbraga.testetecnico.mathsolver.presentation.mvi

import android.net.Uri

sealed interface PhotoSolverIntent {
    data class ImageCaptured(val uri: Uri) : PhotoSolverIntent
    data object Reset : PhotoSolverIntent
}
