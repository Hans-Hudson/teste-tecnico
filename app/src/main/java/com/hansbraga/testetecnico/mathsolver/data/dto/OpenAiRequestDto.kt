package com.hansbraga.testetecnico.mathsolver.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

@Serializable
data class OpenAiResponseRequest(
    val model: String,
    val input: List<InputMessageDto>,
    val text: TextConfigDto
)

@Serializable
data class InputMessageDto(
    val role: String,
    val content: List<InputContentDto>
)

@Serializable
data class InputContentDto(
    val type: String,
    val text: String? = null,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class TextConfigDto(val format: ResponseFormatDto)

@Serializable
data class ResponseFormatDto(
    val type: String,
    val name: String,
    val schema: JsonObject,
    val strict: Boolean
)

val MATH_EXPRESSION_JSON_SCHEMA: JsonObject = buildJsonObject {
    put("type", "object")
    putJsonObject("properties") {
        putJsonObject("hasExpression") { put("type", "boolean") }
        putJsonObject("expression") {
            putJsonArray("type") {
                add(JsonPrimitive("string"))
                add(JsonPrimitive("null"))
            }
        }
        putJsonObject("result") {
            putJsonArray("type") {
                add(JsonPrimitive("string"))
                add(JsonPrimitive("null"))
            }
        }
    }
    putJsonArray("required") {
        add(JsonPrimitive("hasExpression"))
        add(JsonPrimitive("expression"))
        add(JsonPrimitive("result"))
    }
    put("additionalProperties", false)
}
