package com.hansbraga.testetecnico.calculator.domain

import kotlinx.coroutines.flow.Flow

interface CalculatorHistoryRepository {
    fun observeAll(): Flow<List<HistoryItem>>
    suspend fun insert(expression: String, result: String)
    suspend fun delete(id: Long)
    suspend fun clearAll()
}
