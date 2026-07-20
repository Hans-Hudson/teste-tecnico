package com.hansbraga.testetecnico.calculator.presentation.mvi

import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation

sealed interface CalculatorIntent {
    data class DigitPressed(val digit: Int) : CalculatorIntent
    data class OperationPressed(val operation: CalculatorOperation) : CalculatorIntent
    data object DecimalPointPressed : CalculatorIntent
    data object PercentPressed : CalculatorIntent
    data object ToggleSignPressed : CalculatorIntent
    data object EqualsPressed : CalculatorIntent
    data object ClearPressed : CalculatorIntent
}
