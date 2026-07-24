package com.hansbraga.testetecnico.calculator.domain

import androidx.annotation.StringRes
import com.hansbraga.testetecnico.R

enum class CalculatorOperation(val symbol: String, @param:StringRes val accessibilityLabelRes: Int) {
    ADD("+", R.string.calculator_operation_add_description),
    SUBTRACT("-", R.string.calculator_operation_subtract_description),
    MULTIPLY("×", R.string.calculator_operation_multiply_description),
    DIVIDE("÷", R.string.calculator_operation_divide_description)
}
