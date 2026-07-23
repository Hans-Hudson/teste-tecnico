package com.hansbraga.testetecnico.mathsolver.domain

interface MathSolverRepository {
    suspend fun solve(imageBytes: ByteArray): MathSolverResult
}
