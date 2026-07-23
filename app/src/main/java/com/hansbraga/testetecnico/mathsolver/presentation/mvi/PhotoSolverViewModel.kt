package com.hansbraga.testetecnico.mathsolver.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverRepository
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val NOT_FOUND_MESSAGE = "Nenhuma expressão matemática foi encontrada na imagem."

class PhotoSolverViewModel(
    private val repository: MathSolverRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PhotoSolverState>(PhotoSolverState.Idle)
    val state: StateFlow<PhotoSolverState> = _state.asStateFlow()

    fun onIntent(intent: PhotoSolverIntent) {
        when (intent) {
            is PhotoSolverIntent.ImageCaptured -> solve(intent.imageBytes)
            PhotoSolverIntent.Reset -> _state.value = PhotoSolverState.Idle
        }
    }

    private fun solve(imageBytes: ByteArray) {
        _state.value = PhotoSolverState.Loading
        viewModelScope.launch {
            _state.value = when (val result = repository.solve(imageBytes)) {
                is MathSolverResult.Solved -> PhotoSolverState.Success(result.expression, result.result)
                MathSolverResult.ExpressionNotFound -> PhotoSolverState.Error(NOT_FOUND_MESSAGE)
                is MathSolverResult.Error -> PhotoSolverState.Error(result.message)
            }
        }
    }
}
