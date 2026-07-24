package com.hansbraga.testetecnico.calculator.presentation.mvi

import app.cash.turbine.test
import com.hansbraga.testetecnico.calculator.data.fake.FakeCalculatorHistoryRepository
import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.domain.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private val fakeRepository = FakeCalculatorHistoryRepository()
    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = CalculatorViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

    @Test
    fun `equals persists an entry to history`() = runTest {
        viewModel.historyState.test {
            assertEquals(emptyList<HistoryItem>(), awaitItem())

            viewModel.onIntent(CalculatorIntent.DigitPressed(2))
            viewModel.onIntent(CalculatorIntent.OperationPressed(CalculatorOperation.ADD))
            viewModel.onIntent(CalculatorIntent.DigitPressed(3))
            viewModel.onIntent(CalculatorIntent.EqualsPressed)

            val history = awaitItem()
            assertEquals(1, history.size)
            assertEquals("2 + 3", history[0].expression)
            assertEquals("5", history[0].result)
        }
    }

    @Test
    fun `equals after division by zero does not persist history`() = runTest {
        viewModel.historyState.test {
            awaitItem()

            viewModel.onIntent(CalculatorIntent.DigitPressed(5))
            viewModel.onIntent(CalculatorIntent.OperationPressed(CalculatorOperation.DIVIDE))
            viewModel.onIntent(CalculatorIntent.DigitPressed(0))
            viewModel.onIntent(CalculatorIntent.EqualsPressed)

            expectNoEvents()
        }
    }

    @Test
    fun `selecting a history item reuses its result on the display`() = runTest {
        fakeRepository.insert(expression = "2 + 3", result = "5")

        val historyItemId = viewModel.historyState.value.first().id

        viewModel.state.test {
            awaitItem()
            viewModel.onIntent(CalculatorIntent.HistoryItemSelected(historyItemId))
            assertEquals("5", awaitItem().display)
        }
    }

    @Test
    fun `deleting a history item removes it from history`() = runTest {
        fakeRepository.insert(expression = "2 + 3", result = "5")
        val id = viewModel.historyState.value.first().id

        viewModel.historyState.test {
            assertEquals(1, awaitItem().size)

            viewModel.onIntent(CalculatorIntent.DeleteHistoryItem(id))
            assertEquals(emptyList<HistoryItem>(), awaitItem())
        }
    }

    @Test
    fun `clear history pressed removes every entry`() = runTest {
        fakeRepository.insert(expression = "2 + 3", result = "5")
        fakeRepository.insert(expression = "4 + 4", result = "8")

        viewModel.historyState.test {
            assertEquals(2, awaitItem().size)

            viewModel.onIntent(CalculatorIntent.ClearHistoryPressed)
            assertEquals(emptyList<HistoryItem>(), awaitItem())
        }
    }
}
