package com.hansbraga.testetecnico.mathsolver.data.fake

import com.hansbraga.testetecnico.mathsolver.domain.MathSolverRepository
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverResult

class FakeMathSolverRepository : MathSolverRepository {

    var result: MathSolverResult = MathSolverResult.Solved(expression = "2 + 2", result = "4")
    var lastImageBytes: ByteArray? = null

    override suspend fun solve(imageBytes: ByteArray): MathSolverResult {
        lastImageBytes = imageBytes
        return result
    }
}
