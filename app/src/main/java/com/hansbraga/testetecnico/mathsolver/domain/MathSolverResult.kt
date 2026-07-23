package com.hansbraga.testetecnico.mathsolver.domain

sealed interface MathSolverResult {
    data class Solved(val expression: String, val result: String) : MathSolverResult
    data object ExpressionNotFound : MathSolverResult
    data class Error(val message: String) : MathSolverResult
}
