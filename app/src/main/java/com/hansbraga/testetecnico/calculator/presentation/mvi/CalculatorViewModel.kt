package com.hansbraga.testetecnico.calculator.presentation.mvi

import androidx.lifecycle.ViewModel
import com.hansbraga.testetecnico.calculator.domain.CalculatorEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel(engine: CalculatorEngine) : ViewModel() {

    private val reducer = CalculatorReducer(engine)

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onIntent(intent: CalculatorIntent) {
        _state.update { current -> reducer.reduce(current, intent) }
    }
}
