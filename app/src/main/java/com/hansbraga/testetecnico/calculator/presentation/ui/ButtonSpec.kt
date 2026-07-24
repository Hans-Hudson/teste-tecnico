package com.hansbraga.testetecnico.calculator.presentation.ui

import androidx.annotation.StringRes
import com.hansbraga.testetecnico.R
import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorIntent

enum class ButtonRole { EMPHASIS, SECONDARY, NEUTRAL }

sealed interface ButtonSpec {
    data class Digit(val digit: Int) : ButtonSpec
    data class Operation(val operation: CalculatorOperation) : ButtonSpec
    data object Decimal : ButtonSpec
    data object Equals : ButtonSpec
    data object Clear : ButtonSpec
    data object ToggleSign : ButtonSpec
    data object Percent : ButtonSpec
}

val ButtonSpec.role: ButtonRole
    get() = when (this) {
        is ButtonSpec.Digit, ButtonSpec.Decimal -> ButtonRole.NEUTRAL
        is ButtonSpec.Operation, ButtonSpec.Equals -> ButtonRole.EMPHASIS
        ButtonSpec.Clear, ButtonSpec.ToggleSign, ButtonSpec.Percent -> ButtonRole.SECONDARY
    }

fun ButtonSpec.label(): String = when (this) {
    is ButtonSpec.Digit -> digit.toString()
    is ButtonSpec.Operation -> operation.symbol
    ButtonSpec.Decimal -> "."
    ButtonSpec.Equals -> "="
    ButtonSpec.Clear -> "C"
    ButtonSpec.ToggleSign -> "+/-"
    ButtonSpec.Percent -> "%"
}

@StringRes
fun ButtonSpec.accessibilityDescriptionRes(): Int? = when (this) {
    is ButtonSpec.Digit -> null
    is ButtonSpec.Operation -> operation.accessibilityLabelRes
    ButtonSpec.Decimal -> R.string.calculator_button_decimal_description
    ButtonSpec.Equals -> R.string.calculator_button_equals_description
    ButtonSpec.Clear -> R.string.calculator_button_clear_description
    ButtonSpec.ToggleSign -> R.string.calculator_button_toggle_sign_description
    ButtonSpec.Percent -> R.string.calculator_button_percent_description
}

fun ButtonSpec.tag(): String = when (this) {
    is ButtonSpec.Digit -> CalculatorTestTags.digitButton(digit)
    is ButtonSpec.Operation -> CalculatorTestTags.operationButton(operation)
    ButtonSpec.Decimal -> CalculatorTestTags.DECIMAL_BUTTON
    ButtonSpec.Equals -> CalculatorTestTags.EQUALS_BUTTON
    ButtonSpec.Clear -> CalculatorTestTags.CLEAR_BUTTON
    ButtonSpec.ToggleSign -> CalculatorTestTags.TOGGLE_SIGN_BUTTON
    ButtonSpec.Percent -> CalculatorTestTags.PERCENT_BUTTON
}

fun ButtonSpec.toIntent(): CalculatorIntent = when (this) {
    is ButtonSpec.Digit -> CalculatorIntent.DigitPressed(digit)
    is ButtonSpec.Operation -> CalculatorIntent.OperationPressed(operation)
    ButtonSpec.Decimal -> CalculatorIntent.DecimalPointPressed
    ButtonSpec.Equals -> CalculatorIntent.EqualsPressed
    ButtonSpec.Clear -> CalculatorIntent.ClearPressed
    ButtonSpec.ToggleSign -> CalculatorIntent.ToggleSignPressed
    ButtonSpec.Percent -> CalculatorIntent.PercentPressed
}
