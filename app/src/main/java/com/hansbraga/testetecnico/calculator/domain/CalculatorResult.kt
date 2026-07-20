package com.hansbraga.testetecnico.calculator.domain

sealed interface CalculatorResult {
    data class Success(val value: Double) : CalculatorResult
    data class Error(val error: CalculatorError) : CalculatorResult
}
