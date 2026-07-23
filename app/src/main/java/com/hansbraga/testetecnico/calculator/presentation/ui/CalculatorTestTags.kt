package com.hansbraga.testetecnico.calculator.presentation.ui

import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation

object CalculatorTestTags {
    const val DISPLAY = "calculator_display"
    const val DECIMAL_BUTTON = "calculator_decimal"
    const val EQUALS_BUTTON = "calculator_equals"
    const val CLEAR_BUTTON = "calculator_clear"
    const val TOGGLE_SIGN_BUTTON = "calculator_toggle_sign"
    const val PERCENT_BUTTON = "calculator_percent"
    const val HISTORY_LIST = "calculator_history_list"
    const val HISTORY_CLEAR_BUTTON = "calculator_history_clear"
    const val OPEN_PHOTO_SOLVER_BUTTON = "calculator_open_photo_solver"

    fun digitButton(digit: Int) = "calculator_digit_$digit"
    fun operationButton(operation: CalculatorOperation) = "calculator_operation_${operation.name}"
    fun historyItem(id: Long) = "calculator_history_item_$id"
    fun historyItemDelete(id: Long) = "calculator_history_delete_$id"
}
