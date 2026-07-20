package com.hansbraga.testetecnico.calculator.presentation.mvi

import com.hansbraga.testetecnico.calculator.domain.CalculatorEngine
import com.hansbraga.testetecnico.calculator.domain.CalculatorError
import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.domain.CalculatorResult
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorReducerTest {

    private val engine: CalculatorEngine = mockk()
    private lateinit var reducer: CalculatorReducer

    @Before
    fun setUp() {
        reducer = CalculatorReducer(engine)
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
    fun `equals pressed evaluates pending operation using the engine`() {
        every { engine.evaluate(2.0, 3.0, CalculatorOperation.ADD) } returns CalculatorResult.Success(5.0)

        val state = reducer.reduce(
            CalculatorState(display = "3", storedOperand = 2.0, pendingOperation = CalculatorOperation.ADD),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("5", state.display)
        assertEquals(null, state.pendingOperation)
    }

    @Test
    fun `equals pressed with division by zero sets error state`() {
        every { engine.evaluate(5.0, 0.0, CalculatorOperation.DIVIDE) } returns
            CalculatorResult.Error(CalculatorError.DIVISION_BY_ZERO)

        val state = reducer.reduce(
            CalculatorState(display = "0", storedOperand = 5.0, pendingOperation = CalculatorOperation.DIVIDE),
            CalculatorIntent.EqualsPressed
        )

        assertEquals("Erro", state.display)
        assertEquals(true, state.isError)
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
}
