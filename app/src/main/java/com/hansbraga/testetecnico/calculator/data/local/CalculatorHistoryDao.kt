package com.hansbraga.testetecnico.calculator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorHistoryDao {

    @Insert
    suspend fun insert(entity: CalculatorHistoryEntity)

    @Query("SELECT * FROM calculator_history ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<CalculatorHistoryEntity>>

    @Query("DELETE FROM calculator_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calculator_history")
    suspend fun clearAll()
}
