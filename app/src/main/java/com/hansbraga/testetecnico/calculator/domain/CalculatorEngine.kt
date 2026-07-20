package com.hansbraga.testetecnico.calculator.domain

interface CalculatorEngine {
    fun evaluate(first: Double, second: Double, operation: CalculatorOperation): CalculatorResult
}
