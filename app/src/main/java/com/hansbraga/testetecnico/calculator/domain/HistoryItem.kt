package com.hansbraga.testetecnico.calculator.domain

data class HistoryItem(
    val id: Long,
    val expression: String,
    val result: String,
    val timestamp: Long
)
