package com.hansbraga.testetecnico.calculator.presentation.mvi

import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation

data class CalculatorState(
    val display: String = "0",
    val pendingOperation: CalculatorOperation? = null,
    val storedOperand: Double? = null,
    val isError: Boolean = false,
    val shouldResetDisplay: Boolean = false
)
