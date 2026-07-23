package com.hansbraga.testetecnico.calculator.data.fake

import com.hansbraga.testetecnico.calculator.domain.CalculatorHistoryRepository
import com.hansbraga.testetecnico.calculator.domain.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCalculatorHistoryRepository : CalculatorHistoryRepository {

    private val items = MutableStateFlow<List<HistoryItem>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<HistoryItem>> = items.asStateFlow()

    override suspend fun insert(expression: String, result: String) {
        val item = HistoryItem(id = nextId, expression = expression, result = result, timestamp = nextId)
        nextId++
        items.value += item
    }

    override suspend fun delete(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }

    override suspend fun clearAll() {
        items.value = emptyList()
    }
}
