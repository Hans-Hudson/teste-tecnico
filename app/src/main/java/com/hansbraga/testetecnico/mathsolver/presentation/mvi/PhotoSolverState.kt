package com.hansbraga.testetecnico.mathsolver.presentation.mvi

sealed interface PhotoSolverState {
    data object Idle : PhotoSolverState
    data object Loading : PhotoSolverState
    data class Success(val expression: String, val result: String) : PhotoSolverState
    data class Error(val message: String) : PhotoSolverState
}
