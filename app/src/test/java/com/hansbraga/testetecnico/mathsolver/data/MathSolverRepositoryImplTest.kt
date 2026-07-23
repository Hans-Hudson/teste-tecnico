package com.hansbraga.testetecnico.mathsolver.data

import com.hansbraga.testetecnico.mathsolver.data.dto.MathExpressionPayload
import com.hansbraga.testetecnico.mathsolver.data.dto.OpenAiResponseDto
import com.hansbraga.testetecnico.mathsolver.data.dto.OpenAiResponseRequest
import com.hansbraga.testetecnico.mathsolver.data.dto.OutputContentDto
import com.hansbraga.testetecnico.mathsolver.data.dto.OutputItemDto
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class MathSolverRepositoryImplTest {

    private val api = mockk<MathSolverApi>()
    private val json = Json { ignoreUnknownKeys = true }
    private val repository = MathSolverRepositoryImpl(api, json)

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `solved expression maps to Solved`() = runTest {
        coEvery { api.createResponse(any()) } returns responseWithPayload(
            MathExpressionPayload(hasExpression = true, expression = "6 x 7", result = "42")
        )

        val result = repository.solve(byteArrayOf(1, 2, 3))

        assertEquals(MathSolverResult.Solved(expression = "6 x 7", result = "42"), result)
    }

    @Test
    fun `no expression in the image maps to ExpressionNotFound`() = runTest {
        coEvery { api.createResponse(any()) } returns responseWithPayload(
            MathExpressionPayload(hasExpression = false, expression = null, result = null)
        )

        val result = repository.solve(byteArrayOf(1, 2, 3))

        assertEquals(MathSolverResult.ExpressionNotFound, result)
    }

    @Test
    fun `hasExpression true without a result also maps to ExpressionNotFound`() = runTest {
        coEvery { api.createResponse(any()) } returns responseWithPayload(
            MathExpressionPayload(hasExpression = true, expression = "6 x 7", result = null)
        )

        val result = repository.solve(byteArrayOf(1, 2, 3))

        assertEquals(MathSolverResult.ExpressionNotFound, result)
    }

    @Test
    fun `network failure maps to Error`() = runTest {
        coEvery { api.createResponse(any()) } throws IOException("boom")

        val result = repository.solve(byteArrayOf(1, 2, 3))

        assertTrue(result is MathSolverResult.Error)
    }

    @Test
    fun `response without an output_text item maps to Error`() = runTest {
        coEvery { api.createResponse(any()) } returns OpenAiResponseDto(output = emptyList())

        val result = repository.solve(byteArrayOf(1, 2, 3))

        assertTrue(result is MathSolverResult.Error)
    }

    @Test
    fun `unparsable payload text maps to Error`() = runTest {
        coEvery { api.createResponse(any()) } returns OpenAiResponseDto(
            output = listOf(
                OutputItemDto(type = "message", content = listOf(OutputContentDto(type = "output_text", text = "not json")))
            )
        )

        val result = repository.solve(byteArrayOf(1, 2, 3))

        assertTrue(result is MathSolverResult.Error)
    }

    @Test
    fun `image bytes are sent as a base64 data uri`() = runTest {
        var capturedRequest: OpenAiResponseRequest? = null
        coEvery { api.createResponse(any()) } answers {
            capturedRequest = firstArg()
            responseWithPayload(MathExpressionPayload(hasExpression = true, expression = "1 + 1", result = "2"))
        }

        repository.solve(byteArrayOf(1, 2, 3))

        val imageContent = capturedRequest!!.input.first().content.first { it.type == "input_image" }
        assertTrue(imageContent.imageUrl!!.startsWith("data:image/jpeg;base64,"))
    }

    private fun responseWithPayload(payload: MathExpressionPayload): OpenAiResponseDto {
        val text = json.encodeToString(payload)
        return OpenAiResponseDto(
            output = listOf(
                OutputItemDto(type = "message", content = listOf(OutputContentDto(type = "output_text", text = text)))
            )
        )
    }
}
