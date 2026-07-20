package com.hansbraga.testetecnico.calculator.data

import com.hansbraga.testetecnico.calculator.data.local.CalculatorHistoryDao
import com.hansbraga.testetecnico.calculator.data.local.CalculatorHistoryEntity
import com.hansbraga.testetecnico.calculator.domain.CalculatorHistoryRepository
import com.hansbraga.testetecnico.calculator.domain.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CalculatorHistoryRepositoryImpl(private val dao: CalculatorHistoryDao) : CalculatorHistoryRepository {

    override fun observeAll(): Flow<List<HistoryItem>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insert(expression: String, result: String) {
        dao.insert(
            CalculatorHistoryEntity(
                expression = expression,
                result = result,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun clearAll() = dao.clearAll()

    private fun CalculatorHistoryEntity.toDomain() = HistoryItem(
        id = id,
        expression = expression,
        result = result,
        timestamp = timestamp
    )
}
