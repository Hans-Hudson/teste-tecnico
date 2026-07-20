package com.hansbraga.testetecnico.calculator.presentation.mvi

import kotlin.math.abs

private const val LARGE_NUMBER_THRESHOLD = 1e15

internal fun formatOperand(value: Double): String {
    return if (value == value.toLong().toDouble() && abs(value) < LARGE_NUMBER_THRESHOLD) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}
