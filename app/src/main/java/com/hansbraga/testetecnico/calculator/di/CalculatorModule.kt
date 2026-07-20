package com.hansbraga.testetecnico.calculator.di

import com.hansbraga.testetecnico.calculator.domain.CalculatorEngine
import com.hansbraga.testetecnico.calculator.domain.DefaultCalculatorEngine
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calculatorModule = module {
    single<CalculatorEngine> { DefaultCalculatorEngine() }
    viewModel { CalculatorViewModel(get()) }
}
