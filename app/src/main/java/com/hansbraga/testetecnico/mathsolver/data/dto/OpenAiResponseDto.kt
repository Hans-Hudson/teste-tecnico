package com.hansbraga.testetecnico.mathsolver.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiResponseDto(val output: List<OutputItemDto> = emptyList())

@Serializable
data class OutputItemDto(
    val type: String,
    val content: List<OutputContentDto> = emptyList()
)

@Serializable
data class OutputContentDto(
    val type: String,
    val text: String? = null
)

@Serializable
data class MathExpressionPayload(
    val hasExpression: Boolean,
    val expression: String? = null,
    val result: String? = null
)
