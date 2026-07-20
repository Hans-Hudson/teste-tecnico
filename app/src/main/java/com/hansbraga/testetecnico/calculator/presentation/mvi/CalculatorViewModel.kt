package com.hansbraga.testetecnico.calculator.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansbraga.testetecnico.calculator.domain.CalculatorEngine
import com.hansbraga.testetecnico.calculator.domain.CalculatorHistoryRepository
import com.hansbraga.testetecnico.calculator.domain.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(
    engine: CalculatorEngine,
    private val historyRepository: CalculatorHistoryRepository
) : ViewModel() {

    private val reducer = CalculatorReducer(engine)

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    val historyState: StateFlow<List<HistoryItem>> = historyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onIntent(intent: CalculatorIntent) {
        when (intent) {
            is CalculatorIntent.HistoryItemSelected -> selectHistoryItem(intent.id)
            is CalculatorIntent.DeleteHistoryItem -> viewModelScope.launch { historyRepository.delete(intent.id) }
            CalculatorIntent.ClearHistoryPressed -> viewModelScope.launch { historyRepository.clearAll() }
            CalculatorIntent.EqualsPressed -> applyEquals()
            is CalculatorIntent.ArithmeticIntent -> _state.update { current -> reducer.reduce(current, intent) }
        }
    }

    private fun applyEquals() {
        val previous = _state.value
        val next = reducer.reduce(previous, CalculatorIntent.EqualsPressed)
        _state.value = next

        val expression = buildExpression(previous)
        if (!next.isError && expression != null) {
            viewModelScope.launch {
                historyRepository.insert(expression = expression, result = next.display)
            }
        }
    }

    private fun selectHistoryItem(id: Long) {
        val item = historyState.value.firstOrNull { it.id == id } ?: return
        _state.value = CalculatorState(display = item.result, shouldResetDisplay = true)
    }

    private fun buildExpression(state: CalculatorState): String? {
        val operation = state.pendingOperation ?: return null
        val operand = state.storedOperand ?: return null
        return "${formatOperand(operand)} ${operation.symbol} ${state.display}"
    }
}
