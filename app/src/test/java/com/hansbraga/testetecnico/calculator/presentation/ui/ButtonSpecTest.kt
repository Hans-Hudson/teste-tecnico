package com.hansbraga.testetecnico.calculator.presentation.ui

import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ButtonSpecTest {

    @Test
    fun `digit buttons expose their own value as label, tag and intent, with no accessibility description`() {
        for (digit in 0..9) {
            val spec = ButtonSpec.Digit(digit)

            assertEquals(digit.toString(), spec.label())
            assertNull(spec.accessibilityDescription())
            assertEquals(CalculatorTestTags.digitButton(digit), spec.tag())
            assertEquals(CalculatorIntent.DigitPressed(digit), spec.toIntent())
            assertEquals(ButtonRole.NEUTRAL, spec.role)
        }
    }

    @Test
    fun `operation buttons expose the operation symbol, accessibility label, tag and intent`() {
        CalculatorOperation.entries.forEach { operation ->
            val spec = ButtonSpec.Operation(operation)

            assertEquals(operation.symbol, spec.label())
            assertEquals(operation.accessibilityLabel, spec.accessibilityDescription())
            assertEquals(CalculatorTestTags.operationButton(operation), spec.tag())
            assertEquals(CalculatorIntent.OperationPressed(operation), spec.toIntent())
            assertEquals(ButtonRole.EMPHASIS, spec.role)
        }
    }

    @Test
    fun `decimal button exposes its label, accessibility description, tag, intent and role`() {
        val spec = ButtonSpec.Decimal

        assertEquals(".", spec.label())
        assertEquals("Ponto decimal", spec.accessibilityDescription())
        assertEquals(CalculatorTestTags.DECIMAL_BUTTON, spec.tag())
        assertEquals(CalculatorIntent.DecimalPointPressed, spec.toIntent())
        assertEquals(ButtonRole.NEUTRAL, spec.role)
    }

    @Test
    fun `equals button exposes its label, accessibility description, tag, intent and role`() {
        val spec = ButtonSpec.Equals

        assertEquals("=", spec.label())
        assertEquals("Igual", spec.accessibilityDescription())
        assertEquals(CalculatorTestTags.EQUALS_BUTTON, spec.tag())
        assertEquals(CalculatorIntent.EqualsPressed, spec.toIntent())
        assertEquals(ButtonRole.EMPHASIS, spec.role)
    }

    @Test
    fun `clear button exposes its label, accessibility description, tag, intent and role`() {
        val spec = ButtonSpec.Clear

        assertEquals("C", spec.label())
        assertEquals("Limpar calculadora", spec.accessibilityDescription())
        assertEquals(CalculatorTestTags.CLEAR_BUTTON, spec.tag())
        assertEquals(CalculatorIntent.ClearPressed, spec.toIntent())
        assertEquals(ButtonRole.SECONDARY, spec.role)
    }

    @Test
    fun `toggle sign button exposes its label, accessibility description, tag, intent and role`() {
        val spec = ButtonSpec.ToggleSign

        assertEquals("+/-", spec.label())
        assertEquals("Alternar sinal", spec.accessibilityDescription())
        assertEquals(CalculatorTestTags.TOGGLE_SIGN_BUTTON, spec.tag())
        assertEquals(CalculatorIntent.ToggleSignPressed, spec.toIntent())
        assertEquals(ButtonRole.SECONDARY, spec.role)
    }

    @Test
    fun `percent button exposes its label, accessibility description, tag, intent and role`() {
        val spec = ButtonSpec.Percent

        assertEquals("%", spec.label())
        assertEquals("Porcentagem", spec.accessibilityDescription())
        assertEquals(CalculatorTestTags.PERCENT_BUTTON, spec.tag())
        assertEquals(CalculatorIntent.PercentPressed, spec.toIntent())
        assertEquals(ButtonRole.SECONDARY, spec.role)
    }
}
