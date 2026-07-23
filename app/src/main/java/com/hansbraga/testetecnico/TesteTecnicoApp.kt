package com.hansbraga.testetecnico

import android.app.Application
import com.hansbraga.testetecnico.calculator.di.calculatorModule
import com.hansbraga.testetecnico.mathsolver.di.mathSolverModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TesteTecnicoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TesteTecnicoApp)
            modules(calculatorModule, mathSolverModule)
        }
    }
}
