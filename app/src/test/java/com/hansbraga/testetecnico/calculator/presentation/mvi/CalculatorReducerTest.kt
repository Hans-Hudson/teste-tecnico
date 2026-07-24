package com.hansbraga.testetecnico.calculator.presentation.mvi

import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorReducerTest {

    private lateinit var reducer: CalculatorReducer

    @Before
    fun setUp() {
        reducer = CalculatorReducer()
    }

    @Test
    fun `digit pressed appends to display`() {
        val state = reducer.reduce(CalculatorState(), CalculatorIntent.DigitPressed(7))

        assertEquals("7", state.display)
    }

    @Test
    fun `digit pressed after another digit concatenates display`() {
        var state = CalculatorState()
        state = reducer.reduce(state, CalculatorIntent.DigitPressed(1))
        state = reducer.reduce(state, CalculatorIntent.DigitPressed(2))

        assertEquals("12", state.display)
    }

    @Test
    fun `decimal point is only added once`() {
        var state = CalculatorState(display = "1")
        state = reducer.reduce(state, CalculatorIntent.DecimalPointPressed)
        state = reducer.reduce(state, CalculatorIntent.DigitPressed(5))
        state = reducer.reduce(state, CalculatorIntent.DecimalPointPressed)

        assertEquals("1.5", state.display)
    }

    @Test
    fun `operation pressed stores operand and marks display for reset`() {
        val state = reducer.reduce(
            CalculatorState(display = "8"),
            CalculatorIntent.OperationPressed(CalculatorOperation.ADD)
        )

        assertEquals(8.0, state.storedOperand)
        assertEquals(CalculatorOperation.ADD, state.pendingOperation)
        assertEquals(true, state.shouldResetDisplay)
    }

    @Test
    fun `equals pressed evaluates pending addition`() {
        val state = reducer.reduce(
            CalculatorState(display = "3", storedOperand = 2.0, pendingOperation = CalculatorOperation.ADD),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("5", state.display)
        assertEquals(null, state.pendingOperation)
    }

    @Test
    fun `equals pressed evaluates pending subtraction`() {
        val state = reducer.reduce(
            CalculatorState(display = "4", storedOperand = 10.0, pendingOperation = CalculatorOperation.SUBTRACT),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("6", state.display)
    }

    @Test
    fun `equals pressed evaluates pending multiplication`() {
        val state = reducer.reduce(
            CalculatorState(display = "4", storedOperand = 3.0, pendingOperation = CalculatorOperation.MULTIPLY),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("12", state.display)
    }

    @Test
    fun `equals pressed evaluates pending division`() {
        val state = reducer.reduce(
            CalculatorState(display = "2", storedOperand = 9.0, pendingOperation = CalculatorOperation.DIVIDE),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("4.5", state.display)
    }

    @Test
    fun `equals pressed with division by zero sets error state`() {
        val state = reducer.reduce(
            CalculatorState(display = "0", storedOperand = 5.0, pendingOperation = CalculatorOperation.DIVIDE),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("Erro", state.display)
        assertEquals(true, state.isError)
    }

    @Test
    fun `equals pressed with zero divided by zero sets error state`() {
        val state = reducer.reduce(
            CalculatorState(display = "0", storedOperand = 0.0, pendingOperation = CalculatorOperation.DIVIDE),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("Erro", state.display)
        assertEquals(true, state.isError)
    }

    @Test
    fun `pressing an operation while one is already pending evaluates it first`() {
        var state = CalculatorState()
        state = reducer.reduce(state, CalculatorIntent.DigitPressed(2))
        state = reducer.reduce(state, CalculatorIntent.OperationPressed(CalculatorOperation.ADD))
        state = reducer.reduce(state, CalculatorIntent.DigitPressed(3))

        state = reducer.reduce(state, CalculatorIntent.OperationPressed(CalculatorOperation.MULTIPLY))

        assertEquals("5", state.display)
        assertEquals(5.0, state.storedOperand)
        assertEquals(CalculatorOperation.MULTIPLY, state.pendingOperation)
    }

    @Test
    fun `intents are ignored while in error state except clear`() {
        val errorState = CalculatorState(display = "Erro", isError = true)

        val state = reducer.reduce(errorState, CalculatorIntent.DigitPressed(5))

        assertEquals(errorState, state)
    }

    @Test
    fun `clear pressed resets state even from an error`() {
        val errorState = CalculatorState(display = "Erro", isError = true)

        val state = reducer.reduce(errorState, CalculatorIntent.ClearPressed)

        assertEquals(CalculatorState(), state)
    }

    @Test
    fun `toggle sign flips the sign of the display`() {
        val state = reducer.reduce(CalculatorState(display = "42"), CalculatorIntent.ToggleSignPressed)

        assertEquals("-42", state.display)
    }

    @Test
    fun `toggle sign on zero keeps display unchanged`() {
        val state = reducer.reduce(CalculatorState(display = "0"), CalculatorIntent.ToggleSignPressed)

        assertEquals("0", state.display)
    }

    @Test
    fun `percent divides current display by one hundred`() {
        val state = reducer.reduce(CalculatorState(display = "50"), CalculatorIntent.PercentPressed)

        assertEquals("0.5", state.display)
    }

    @Test
    fun `expression in progress combines stored operand, operation symbol and current display`() {
        val state = CalculatorState(display = "3", storedOperand = 2.0, pendingOperation = CalculatorOperation.ADD)

        val expression = reducer.expressionInProgress(state)

        assertEquals("2 + 3", expression)
    }

    @Test
    fun `expression in progress is null when there is no pending operation`() {
        val state = CalculatorState(display = "3", storedOperand = 2.0, pendingOperation = null)

        val expression = reducer.expressionInProgress(state)

        assertEquals(null, expression)
    }

    @Test
    fun `expression in progress is null when there is no stored operand`() {
        val state = CalculatorState(display = "3", storedOperand = null, pendingOperation = CalculatorOperation.ADD)

        val expression = reducer.expressionInProgress(state)

        assertEquals(null, expression)
    }

    @Test
    fun `history selection returns a fresh state pointing at the selected result`() {
        val state = reducer.applyHistorySelection("42")

        assertEquals(CalculatorState(display = "42", shouldResetDisplay = true), state)
    }
}
