package com.hansbraga.testetecnico.mathsolver.presentation.mvi

import android.net.Uri
import app.cash.turbine.test
import com.hansbraga.testetecnico.mathsolver.data.fake.FakeImageCapture
import com.hansbraga.testetecnico.mathsolver.data.fake.FakeMathSolverRepository
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverResult
import io.mockk.mockk
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
    private val fakeImageCapture = FakeImageCapture()
    private val uri: Uri = mockk(relaxed = true)
    private lateinit var viewModel: PhotoSolverViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = PhotoSolverViewModel(fakeRepository, fakeImageCapture)
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
    fun `createCaptureUri delegates to the image capture module`() {
        fakeImageCapture.captureUri = uri

        assertEquals(uri, viewModel.createCaptureUri())
    }

    @Test
    fun `image captured with a solved expression emits Loading then Success`() = runTest {
        fakeRepository.result = MathSolverResult.Solved(expression = "6 x 7", result = "42")

        viewModel.state.test {
            assertEquals(PhotoSolverState.Idle, awaitItem())

            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))

            assertEquals(PhotoSolverState.Loading, awaitItem())
            assertEquals(PhotoSolverState.Success("6 x 7", "42"), awaitItem())
        }
    }

    @Test
    fun `image without an expression emits Error`() = runTest {
        fakeRepository.result = MathSolverResult.ExpressionNotFound

        viewModel.state.test {
            awaitItem()

            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))

            assertEquals(PhotoSolverState.Loading, awaitItem())
            assert(awaitItem() is PhotoSolverState.Error)
        }
    }

    @Test
    fun `repository error is forwarded as the Error message`() = runTest {
        fakeRepository.result = MathSolverResult.Error("Falha de conexão")

        viewModel.state.test {
            awaitItem()

            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))

            assertEquals(PhotoSolverState.Loading, awaitItem())
            assertEquals(PhotoSolverState.Error("Falha de conexão"), awaitItem())
        }
    }

    @Test
    fun `reset returns to Idle`() = runTest {
        viewModel.state.test {
            awaitItem()
            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))
            awaitItem()
            awaitItem()

            viewModel.onIntent(PhotoSolverIntent.Reset)
            assertEquals(PhotoSolverState.Idle, awaitItem())
        }
    }

    @Test
    fun `captured uri is compressed and the resulting bytes are forwarded to the repository`() = runTest {
        val bytes = byteArrayOf(9, 8, 7)
        fakeImageCapture.compressedBytes = bytes

        viewModel.state.test {
            awaitItem()
            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))
            awaitItem()
            awaitItem()
        }

        assertEquals(uri, fakeImageCapture.lastCompressedUri)
        assertEquals(bytes, fakeRepository.lastImageBytes)
    }

    @Test
    fun `image that fails to compress does not trigger loading or reach the repository`() = runTest {
        fakeImageCapture.compressedBytes = null

        viewModel.state.test {
            assertEquals(PhotoSolverState.Idle, awaitItem())
            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))
            expectNoEvents()
        }
        assertEquals(null, fakeRepository.lastImageBytes)
    }
}
