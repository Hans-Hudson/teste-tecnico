package com.hansbraga.testetecnico.calculator.presentation.mvi

import app.cash.turbine.test
import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.domain.DefaultCalculatorEngine
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel(DefaultCalculatorEngine())
    }

    @Test
    fun `initial state shows zero on the display`() = runTest {
        viewModel.state.test {
            assertEquals("0", awaitItem().display)
        }
    }

    @Test
    fun `pressing digits then equals emits the calculated result`() = runTest {
        viewModel.state.test {
            assertEquals("0", awaitItem().display)

            viewModel.onIntent(CalculatorIntent.DigitPressed(2))
            assertEquals("2", awaitItem().display)

            viewModel.onIntent(CalculatorIntent.OperationPressed(CalculatorOperation.ADD))
            assertEquals("2", awaitItem().display)

            viewModel.onIntent(CalculatorIntent.DigitPressed(3))
            assertEquals("3", awaitItem().display)

            viewModel.onIntent(CalculatorIntent.EqualsPressed)
            assertEquals("5", awaitItem().display)
        }
    }

    @Test
    fun `clear after error recovers the calculator`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onIntent(CalculatorIntent.DigitPressed(5))
            awaitItem()
            viewModel.onIntent(CalculatorIntent.OperationPressed(CalculatorOperation.DIVIDE))
            awaitItem()
            viewModel.onIntent(CalculatorIntent.DigitPressed(0))
            awaitItem()
            viewModel.onIntent(CalculatorIntent.EqualsPressed)
            assertEquals(true, awaitItem().isError)

            viewModel.onIntent(CalculatorIntent.ClearPressed)
            assertEquals(CalculatorState(), awaitItem())
        }
    }
}
