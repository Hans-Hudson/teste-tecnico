package com.hansbraga.testetecnico.calculator.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CalculatorHistoryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: CalculatorHistoryDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.calculatorHistoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert then observeAll emits the inserted entry`() = runTest {
        dao.insert(CalculatorHistoryEntity(expression = "2 + 3", result = "5", timestamp = 1L))

        dao.observeAll().test {
            val history = awaitItem()
            assertEquals(1, history.size)
            assertEquals("2 + 3", history[0].expression)
            assertEquals("5", history[0].result)
        }
    }

    @Test
    fun `observeAll orders entries by timestamp descending`() = runTest {
        dao.insert(CalculatorHistoryEntity(expression = "1 + 1", result = "2", timestamp = 1L))
        dao.insert(CalculatorHistoryEntity(expression = "2 + 2", result = "4", timestamp = 2L))

        dao.observeAll().test {
            val history = awaitItem()
            assertEquals("2 + 2", history[0].expression)
            assertEquals("1 + 1", history[1].expression)
        }
    }

    @Test
    fun `deleteById removes only the targeted entry`() = runTest {
        dao.insert(CalculatorHistoryEntity(expression = "1 + 1", result = "2", timestamp = 1L))
        dao.insert(CalculatorHistoryEntity(expression = "2 + 2", result = "4", timestamp = 2L))

        var idToDelete = -1L
        dao.observeAll().test {
            idToDelete = awaitItem().first { it.expression == "1 + 1" }.id
            cancelAndIgnoreRemainingEvents()
        }

        dao.deleteById(idToDelete)

        dao.observeAll().test {
            val history = awaitItem()
            assertEquals(1, history.size)
            assertEquals("2 + 2", history[0].expression)
        }
    }

    @Test
    fun `clearAll removes every entry`() = runTest {
        dao.insert(CalculatorHistoryEntity(expression = "1 + 1", result = "2", timestamp = 1L))
        dao.insert(CalculatorHistoryEntity(expression = "2 + 2", result = "4", timestamp = 2L))

        dao.clearAll()

        dao.observeAll().test {
            assertTrue(awaitItem().isEmpty())
        }
    }
}
