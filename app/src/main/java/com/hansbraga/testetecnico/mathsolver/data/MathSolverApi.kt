package com.hansbraga.testetecnico.mathsolver.data

import com.hansbraga.testetecnico.mathsolver.data.dto.OpenAiResponseDto
import com.hansbraga.testetecnico.mathsolver.data.dto.OpenAiResponseRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface MathSolverApi {
    @POST("responses")
    suspend fun createResponse(@Body request: OpenAiResponseRequest): OpenAiResponseDto
}
