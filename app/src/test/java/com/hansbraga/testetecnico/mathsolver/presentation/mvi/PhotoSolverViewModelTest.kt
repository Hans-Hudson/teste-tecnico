package com.hansbraga.testetecnico.mathsolver.presentation.mvi

import app.cash.turbine.test
import com.hansbraga.testetecnico.mathsolver.data.fake.FakeMathSolverRepository
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverResult
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
class PhotoSolverViewModelTest {

    private val fakeRepository = FakeMathSolverRepository()
    private lateinit var viewModel: PhotoSolverViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = PhotoSolverViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() = runTest {
        viewModel.state.test {
            assertEquals(PhotoSolverState.Idle, awaitItem())
        }
    }

    @Test
    fun `image captured with a solved expression emits Loading then Success`() = runTest {
        fakeRepository.result = MathSolverResult.Solved(expression = "6 x 7", result = "42")

        viewModel.state.test {
            assertEquals(PhotoSolverState.Idle, awaitItem())

            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(byteArrayOf(1, 2, 3)))

            assertEquals(PhotoSolverState.Loading, awaitItem())
            assertEquals(PhotoSolverState.Success("6 x 7", "42"), awaitItem())
        }
    }

    @Test
    fun `image without an expression emits Error`() = runTest {
        fakeRepository.result = MathSolverResult.ExpressionNotFound

        viewModel.state.test {
            awaitItem()

            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(byteArrayOf(1)))

            assertEquals(PhotoSolverState.Loading, awaitItem())
            assert(awaitItem() is PhotoSolverState.Error)
        }
    }

    @Test
    fun `repository error is forwarded as the Error message`() = runTest {
        fakeRepository.result = MathSolverResult.Error("Falha de conexão")

        viewModel.state.test {
            awaitItem()

            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(byteArrayOf(1)))

            assertEquals(PhotoSolverState.Loading, awaitItem())
            assertEquals(PhotoSolverState.Error("Falha de conexão"), awaitItem())
        }
    }

    @Test
    fun `reset returns to Idle`() = runTest {
        viewModel.state.test {
            awaitItem()
            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(byteArrayOf(1)))
            awaitItem()
            awaitItem()

            viewModel.onIntent(PhotoSolverIntent.Reset)
            assertEquals(PhotoSolverState.Idle, awaitItem())
        }
    }

    @Test
    fun `image bytes are forwarded to the repository unchanged`() = runTest {
        val bytes = byteArrayOf(9, 8, 7)

        viewModel.state.test {
            awaitItem()
            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(bytes))
            awaitItem()
            awaitItem()
        }

        assertEquals(bytes, fakeRepository.lastImageBytes)
    }
}
