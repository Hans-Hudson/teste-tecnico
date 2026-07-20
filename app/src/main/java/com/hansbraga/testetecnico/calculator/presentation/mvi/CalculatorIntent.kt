package com.hansbraga.testetecnico.calculator.presentation.mvi

import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation

sealed interface CalculatorIntent {

    /** Intents que alimentam a maquina de estados pura da calculadora. */
    sealed interface ArithmeticIntent : CalculatorIntent

    data class DigitPressed(val digit: Int) : ArithmeticIntent
    data class OperationPressed(val operation: CalculatorOperation) : ArithmeticIntent
    data object DecimalPointPressed : ArithmeticIntent
    data object PercentPressed : ArithmeticIntent
    data object ToggleSignPressed : ArithmeticIntent
    data object EqualsPressed : ArithmeticIntent
    data object ClearPressed : ArithmeticIntent

    data class HistoryItemSelected(val id: Long) : CalculatorIntent
    data class DeleteHistoryItem(val id: Long) : CalculatorIntent
    data object ClearHistoryPressed : CalculatorIntent
}
