package com.hansbraga.testetecnico.mathsolver.data

import android.util.Base64
import android.util.Log
import com.hansbraga.testetecnico.mathsolver.data.dto.InputContentDto
import com.hansbraga.testetecnico.mathsolver.data.dto.InputMessageDto
import com.hansbraga.testetecnico.mathsolver.data.dto.MATH_EXPRESSION_JSON_SCHEMA
import com.hansbraga.testetecnico.mathsolver.data.dto.MathExpressionPayload
import com.hansbraga.testetecnico.mathsolver.data.dto.OpenAiResponseDto
import com.hansbraga.testetecnico.mathsolver.data.dto.OpenAiResponseRequest
import com.hansbraga.testetecnico.mathsolver.data.dto.ResponseFormatDto
import com.hansbraga.testetecnico.mathsolver.data.dto.TextConfigDto
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverRepository
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverResult
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

private const val MODEL = "gpt-4o-mini"
private const val SCHEMA_NAME = "mathexpression"
private const val PROMPT =
    "A imagem a seguir pode conter uma expressão matemática escrita à mão ou impressa. " +
        "Se houver uma expressão, resolva-a e informe a expressão original e o resultado. " +
        "Se não houver nenhuma expressão matemática visível na imagem, informe isso."
private const val UNEXPECTED_RESPONSE_MESSAGE = "Resposta inesperada do serviço. Tente novamente."
private const val REQUEST_FAILED_MESSAGE = "Não foi possível consultar o serviço. Verifique sua internet e tente novamente."

class MathSolverRepositoryImpl(
    private val api: MathSolverApi,
    private val json: Json
) : MathSolverRepository {

    override suspend fun solve(imageBytes: ByteArray): MathSolverResult {
        return try {
            val response = api.createResponse(buildRequest(imageBytes))
            parseResponse(response)
        } catch (e: CancellationException) {
            Log.e("CancellationException", e.message.orEmpty(),e)
            throw e
        } catch (e: Exception) {
            Log.e("Exception", e.message.orEmpty(),e)
            MathSolverResult.Error(REQUEST_FAILED_MESSAGE)
        }
    }

    private fun buildRequest(imageBytes: ByteArray): OpenAiResponseRequest {
        val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        val dataUri = "data:image/jpeg;base64,$base64Image"

        return OpenAiResponseRequest(
            model = MODEL,
            input = listOf(
                InputMessageDto(
                    role = "user",
                    content = listOf(
                        InputContentDto(type = "input_text", text = PROMPT),
                        InputContentDto(type = "input_image", imageUrl = dataUri)
                    )
                )
            ),
            text = TextConfigDto(
                format = ResponseFormatDto(
                    type = "json_schema",
                    name = SCHEMA_NAME,
                    schema = MATH_EXPRESSION_JSON_SCHEMA,
                    strict = true
                )
            )
        )
    }

    private fun parseResponse(response: OpenAiResponseDto): MathSolverResult {
        val payloadText = response.output
            .firstOrNull { it.type == "message" }
            ?.content
            ?.firstOrNull { it.type == "output_text" }
            ?.text
            ?: return MathSolverResult.Error(UNEXPECTED_RESPONSE_MESSAGE)

        val payload = try {
            json.decodeFromString(MathExpressionPayload.serializer(), payloadText)
        } catch (e: SerializationException) {
            return MathSolverResult.Error(UNEXPECTED_RESPONSE_MESSAGE)
        }

        return if (payload.hasExpression && payload.result != null) {
            MathSolverResult.Solved(expression = payload.expression.orEmpty(), result = payload.result)
        } else {
            MathSolverResult.ExpressionNotFound
        }
    }
}
