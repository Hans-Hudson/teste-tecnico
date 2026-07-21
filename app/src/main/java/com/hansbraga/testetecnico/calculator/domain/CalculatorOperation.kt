package com.hansbraga.testetecnico.calculator.domain

enum class CalculatorOperation(val symbol: String, val accessibilityLabel: String) {
    ADD("+", "Somar"),
    SUBTRACT("-", "Subtrair"),
    MULTIPLY("×", "Multiplicar"),
    DIVIDE("÷", "Dividir")
}
