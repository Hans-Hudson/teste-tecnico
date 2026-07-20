package com.hansbraga.testetecnico.calculator.domain

class DefaultCalculatorEngine : CalculatorEngine {

    override fun evaluate(first: Double, second: Double, operation: CalculatorOperation): CalculatorResult {
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
}
