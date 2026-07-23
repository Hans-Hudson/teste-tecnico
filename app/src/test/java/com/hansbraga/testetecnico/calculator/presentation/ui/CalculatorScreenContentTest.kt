package com.hansbraga.testetecnico.calculator.presentation.ui

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.domain.HistoryItem
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorIntent
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorState
import com.hansbraga.testetecnico.core.theme.TesteTecnicoTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w360dp-h640dp")
class CalculatorScreenContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val recordedIntents = mutableListOf<CalculatorIntent>()

    @After
    fun tearDown() {
        stopKoin()
    }

    private fun setContent(
        state: CalculatorState = CalculatorState(),
        history: List<HistoryItem> = emptyList()
    ) {
        composeTestRule.setContent {
            TesteTecnicoTheme {
                CalculatorScreenContent(
                    state = state,
                    history = history,
                    onIntent = { recordedIntents.add(it) }
                )
            }
        }
    }

    @Test
    fun `tapping a digit emits DigitPressed`() {
        setContent()

        composeTestRule.onNodeWithTag(CalculatorTestTags.digitButton(7)).performClick()

        assertEquals(listOf(CalculatorIntent.DigitPressed(7)), recordedIntents)
    }

    @Test
    fun `tapping an operation emits OperationPressed`() {
        setContent()

        composeTestRule.onNodeWithTag(CalculatorTestTags.operationButton(CalculatorOperation.ADD)).performClick()

        assertEquals(listOf(CalculatorIntent.OperationPressed(CalculatorOperation.ADD)), recordedIntents)
    }

    @Test
    fun `tapping decimal emits DecimalPointPressed`() {
        setContent()

        composeTestRule.onNodeWithTag(CalculatorTestTags.DECIMAL_BUTTON).performClick()

        assertEquals(listOf(CalculatorIntent.DecimalPointPressed), recordedIntents)
    }

    @Test
    fun `tapping equals emits EqualsPressed`() {
        setContent()

        composeTestRule.onNodeWithTag(CalculatorTestTags.EQUALS_BUTTON).performClick()

        assertEquals(listOf(CalculatorIntent.EqualsPressed), recordedIntents)
    }

    @Test
    fun `tapping clear emits ClearPressed`() {
        setContent()

        composeTestRule.onNodeWithTag(CalculatorTestTags.CLEAR_BUTTON).performClick()

        assertEquals(listOf(CalculatorIntent.ClearPressed), recordedIntents)
    }

    @Test
    fun `tapping toggle sign emits ToggleSignPressed`() {
        setContent()

        composeTestRule.onNodeWithTag(CalculatorTestTags.TOGGLE_SIGN_BUTTON).performClick()

        assertEquals(listOf(CalculatorIntent.ToggleSignPressed), recordedIntents)
    }

    @Test
    fun `tapping percent emits PercentPressed`() {
        setContent()

        composeTestRule.onNodeWithTag(CalculatorTestTags.PERCENT_BUTTON).performClick()

        assertEquals(listOf(CalculatorIntent.PercentPressed), recordedIntents)
    }

    @Test
    fun `normal state shows display with result content description`() {
        setContent(state = CalculatorState(display = "42", isError = false))

        composeTestRule.onNodeWithTag(CalculatorTestTags.DISPLAY)
            .assertContentDescriptionEquals("Resultado: 42")
    }

    @Test
    fun `error state shows display with raw text as content description`() {
        setContent(state = CalculatorState(display = "Erro", isError = true))

        composeTestRule.onNodeWithTag(CalculatorTestTags.DISPLAY)
            .assertContentDescriptionEquals("Erro")
    }

    @Test
    fun `clear history button is disabled when history is empty`() {
        setContent(history = emptyList())

        composeTestRule.onNodeWithTag(CalculatorTestTags.HISTORY_CLEAR_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun `clear history button is enabled when history has items`() {
        setContent(history = listOf(HistoryItem(id = 1, expression = "2 + 2", result = "4", timestamp = 1L)))

        composeTestRule.onNodeWithTag(CalculatorTestTags.HISTORY_CLEAR_BUTTON).assertIsEnabled()
    }

    @Test
    fun `tapping a history item emits HistoryItemSelected`() {
        setContent(history = listOf(HistoryItem(id = 7, expression = "2 + 2", result = "4", timestamp = 1L)))

        composeTestRule.onNodeWithTag(CalculatorTestTags.historyItem(7)).performClick()

        assertEquals(listOf(CalculatorIntent.HistoryItemSelected(7)), recordedIntents)
    }

    @Test
    fun `tapping a history item's delete button emits DeleteHistoryItem`() {
        setContent(history = listOf(HistoryItem(id = 7, expression = "2 + 2", result = "4", timestamp = 1L)))

        composeTestRule.onNodeWithTag(CalculatorTestTags.historyItemDelete(7)).performClick()

        assertEquals(listOf(CalculatorIntent.DeleteHistoryItem(7)), recordedIntents)
    }

    @Test
    fun `tapping clear history emits ClearHistoryPressed`() {
        setContent(history = listOf(HistoryItem(id = 7, expression = "2 + 2", result = "4", timestamp = 1L)))

        composeTestRule.onNodeWithTag(CalculatorTestTags.HISTORY_CLEAR_BUTTON).performClick()

        assertEquals(listOf(CalculatorIntent.ClearHistoryPressed), recordedIntents)
    }
}
