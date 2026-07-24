package com.hansbraga.testetecnico.calculator.di

import androidx.room.Room
import com.hansbraga.testetecnico.calculator.data.CalculatorHistoryRepositoryImpl
import com.hansbraga.testetecnico.calculator.data.local.AppDatabase
import com.hansbraga.testetecnico.calculator.domain.CalculatorHistoryRepository
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private const val DATABASE_NAME = "calculator_database.db"

val calculatorModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, DATABASE_NAME).build()
    }
    single { get<AppDatabase>().calculatorHistoryDao() }
    single<CalculatorHistoryRepository> { CalculatorHistoryRepositoryImpl(get()) }

    viewModel { CalculatorViewModel(get()) }
}
