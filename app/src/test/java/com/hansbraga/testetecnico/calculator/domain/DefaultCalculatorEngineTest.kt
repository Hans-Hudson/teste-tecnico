package com.hansbraga.testetecnico.calculator.domain

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultCalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = DefaultCalculatorEngine()
    }

    @Test
    fun `add returns sum of both operands`() {
        val result = engine.evaluate(2.0, 3.0, CalculatorOperation.ADD)

        assertEquals(CalculatorResult.Success(5.0), result)
    }

    @Test
    fun `subtract returns difference of both operands`() {
        val result = engine.evaluate(10.0, 4.0, CalculatorOperation.SUBTRACT)

        assertEquals(CalculatorResult.Success(6.0), result)
    }

    @Test
    fun `multiply returns product of both operands`() {
        val result = engine.evaluate(3.0, 4.0, CalculatorOperation.MULTIPLY)

        assertEquals(CalculatorResult.Success(12.0), result)
    }

    @Test
    fun `divide returns quotient of both operands`() {
        val result = engine.evaluate(9.0, 2.0, CalculatorOperation.DIVIDE)

        assertEquals(CalculatorResult.Success(4.5), result)
    }

    @Test
    fun `divide by zero returns division by zero error`() {
        val result = engine.evaluate(5.0, 0.0, CalculatorOperation.DIVIDE)

        assertEquals(CalculatorResult.Error(CalculatorError.DIVISION_BY_ZERO), result)
    }

    @Test
    fun `zero divided by zero returns division by zero error`() {
        val result = engine.evaluate(0.0, 0.0, CalculatorOperation.DIVIDE)

        assertEquals(CalculatorResult.Error(CalculatorError.DIVISION_BY_ZERO), result)
    }
}
