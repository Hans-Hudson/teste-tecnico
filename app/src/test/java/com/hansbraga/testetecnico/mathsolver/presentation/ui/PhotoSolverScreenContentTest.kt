package com.hansbraga.testetecnico.mathsolver.presentation.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hansbraga.testetecnico.core.theme.TesteTecnicoTheme
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverState
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
class PhotoSolverScreenContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var takePhotoCalls = 0
    private var pickFromGalleryCalls = 0
    private var resetCalls = 0
    private var navigateBackCalls = 0

    @After
    fun tearDown() {
        stopKoin()
    }

    private fun setContent(state: PhotoSolverState) {
        composeTestRule.setContent {
            TesteTecnicoTheme {
                PhotoSolverScreenContent(
                    state = state,
                    onTakePhoto = { takePhotoCalls++ },
                    onPickFromGallery = { pickFromGalleryCalls++ },
                    onReset = { resetCalls++ },
                    onNavigateBack = { navigateBackCalls++ }
                )
            }
        }
    }

    @Test
    fun `tapping back navigates back`() {
        setContent(PhotoSolverState.Idle)

        composeTestRule.onNodeWithTag(PhotoSolverTestTags.BACK_BUTTON).performClick()

        assertEquals(1, navigateBackCalls)
    }

    @Test
    fun `tapping take photo triggers the callback`() {
        setContent(PhotoSolverState.Idle)

        composeTestRule.onNodeWithTag(PhotoSolverTestTags.TAKE_PHOTO_BUTTON).performClick()

        assertEquals(1, takePhotoCalls)
    }

    @Test
    fun `tapping pick from gallery triggers the callback`() {
        setContent(PhotoSolverState.Idle)

        composeTestRule.onNodeWithTag(PhotoSolverTestTags.PICK_PHOTO_BUTTON).performClick()

        assertEquals(1, pickFromGalleryCalls)
    }

    @Test
    fun `loading state shows the progress indicator and disables the action buttons`() {
        setContent(PhotoSolverState.Loading)

        composeTestRule.onNodeWithTag(PhotoSolverTestTags.LOADING_INDICATOR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(PhotoSolverTestTags.TAKE_PHOTO_BUTTON).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(PhotoSolverTestTags.PICK_PHOTO_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun `success state shows the expression and result`() {
        setContent(PhotoSolverState.Success(expression = "6 x 7", result = "42"))

        composeTestRule.onNodeWithTag(PhotoSolverTestTags.RESULT_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText("6 x 7 = 42").assertIsDisplayed()
    }

    @Test
    fun `error state shows the error message`() {
        setContent(PhotoSolverState.Error("Nenhuma expressão matemática foi encontrada na imagem."))

        composeTestRule.onNodeWithTag(PhotoSolverTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun `tapping reset after a success clears the result`() {
        setContent(PhotoSolverState.Success(expression = "2 + 2", result = "4"))

        composeTestRule.onNodeWithTag(PhotoSolverTestTags.RESET_BUTTON).performClick()

        assertEquals(1, resetCalls)
    }

    @Test
    fun `reset button is not shown in the idle state`() {
        setContent(PhotoSolverState.Idle)

        composeTestRule.onAllNodesWithTag(PhotoSolverTestTags.RESET_BUTTON).assertCountEquals(0)
    }
}
