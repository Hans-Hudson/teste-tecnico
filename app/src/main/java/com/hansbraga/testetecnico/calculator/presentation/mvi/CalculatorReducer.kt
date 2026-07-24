package com.hansbraga.testetecnico.calculator.presentation.mvi

import com.hansbraga.testetecnico.calculator.domain.CalculatorError
import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.domain.CalculatorResult

private const val MAX_DISPLAY_LENGTH = 15

/**
 * Reducer puro: (estado atual, intent) -> novo estado.
 * Mantido sem dependência de Android/ViewModel para ser testável isoladamente.
 */
class CalculatorReducer {

    fun reduce(state: CalculatorState, intent: CalculatorIntent.ArithmeticIntent): CalculatorState {
        if (state.isError && intent != CalculatorIntent.ClearPressed) {
            return state
        }
        return when (intent) {
            is CalculatorIntent.DigitPressed -> applyDigit(state, intent.digit)
            is CalculatorIntent.OperationPressed -> applyOperation(state, intent.operation)
            CalculatorIntent.DecimalPointPressed -> applyDecimalPoint(state)
            CalculatorIntent.PercentPressed -> applyPercent(state)
            CalculatorIntent.ToggleSignPressed -> applyToggleSign(state)
            CalculatorIntent.EqualsPressed -> applyEquals(state)
            CalculatorIntent.ClearPressed -> CalculatorState()
        }
    }

    private fun applyDigit(state: CalculatorState, digit: Int): CalculatorState {
        if (state.display.length >= MAX_DISPLAY_LENGTH && !state.shouldResetDisplay) return state
        val newDisplay = if (state.shouldResetDisplay || state.display == "0") {
            digit.toString()
        } else {
            state.display + digit
        }
        return state.copy(display = newDisplay, shouldResetDisplay = false)
    }

    private fun applyDecimalPoint(state: CalculatorState): CalculatorState {
        if (state.shouldResetDisplay) {
            return state.copy(display = "0.", shouldResetDisplay = false)
        }
        if (state.display.contains('.')) return state
        return state.copy(display = state.display + ".")
    }

    private fun applyOperation(state: CalculatorState, operation: CalculatorOperation): CalculatorState {
        if (state.pendingOperation != null && !state.shouldResetDisplay) {
            val second = state.display.toDouble()
            val first = state.storedOperand ?: second
            return when (val result = evaluate(first, second, state.pendingOperation)) {
                is CalculatorResult.Success -> state.copy(
                    display = formatOperand(result.value),
                    storedOperand = result.value,
                    pendingOperation = operation,
                    shouldResetDisplay = true
                )

                is CalculatorResult.Error -> errorState(result.error)
            }
        }
        val operand = state.display.toDouble()
        return state.copy(storedOperand = operand, pendingOperation = operation, shouldResetDisplay = true)
    }

    private fun applyEquals(state: CalculatorState): CalculatorState {
        val operation = state.pendingOperation ?: return state
        val second = state.display.toDouble()
        val first = state.storedOperand ?: second
        return when (val result = evaluate(first, second, operation)) {
            is CalculatorResult.Success -> CalculatorState(
                display = formatOperand(result.value),
                shouldResetDisplay = true
            )

            is CalculatorResult.Error -> errorState(result.error)
        }
    }

    private fun applyPercent(state: CalculatorState): CalculatorState {
        val value = state.display.toDouble() / 100.0
        return state.copy(display = formatOperand(value), shouldResetDisplay = true)
    }

    private fun applyToggleSign(state: CalculatorState): CalculatorState {
        if (state.display == "0") return state
        val newDisplay = if (state.display.startsWith("-")) {
            state.display.removePrefix("-")
        } else {
            "-${state.display}"
        }
        return state.copy(display = newDisplay)
    }

    private fun evaluate(first: Double, second: Double, operation: CalculatorOperation): CalculatorResult {
        return when (operation) {
            CalculatorOperation.ADD -> CalculatorResult.Success(first + second)
            CalculatorOperation.SUBTRACT -> CalculatorResult.Success(first - second)
            CalculatorOperation.MULTIPLY -> CalculatorResult.Success(first * second)
            CalculatorOperation.DIVIDE -> {
                if (second == 0.0) {
                    CalculatorResult.Error(CalculatorError.DIVISION_BY_ZERO)
                } else {
                    CalculatorResult.Success(first / second)
                }
            }
        }
    }

    private fun errorState(error: CalculatorError): CalculatorState {
        val message = when (error) {
            CalculatorError.DIVISION_BY_ZERO -> "Erro"
        }
        return CalculatorState(display = message, isError = true)
    }

    fun expressionInProgress(state: CalculatorState): String? {
        val operation = state.pendingOperation ?: return null
        val operand = state.storedOperand ?: return null
        return "${formatOperand(operand)} ${operation.symbol} ${state.display}"
    }

    fun applyHistorySelection(result: String): CalculatorState =
        CalculatorState(display = result, shouldResetDisplay = true)
}
