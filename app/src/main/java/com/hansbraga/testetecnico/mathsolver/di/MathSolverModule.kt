package com.hansbraga.testetecnico.mathsolver.di

import com.hansbraga.testetecnico.BuildConfig
import com.hansbraga.testetecnico.mathsolver.data.AndroidImageCapture
import com.hansbraga.testetecnico.mathsolver.data.ImageCapture
import com.hansbraga.testetecnico.mathsolver.data.MathSolverApi
import com.hansbraga.testetecnico.mathsolver.data.MathSolverRepositoryImpl
import com.hansbraga.testetecnico.mathsolver.data.openAiAuthInterceptor
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverRepository
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val OPENAI_BASE_URL = "https://api.openai.com/v1/"

val mathSolverModule = module {
    single { Json { ignoreUnknownKeys = true } }

    single {
        OkHttpClient.Builder()
            .addInterceptor(openAiAuthInterceptor(BuildConfig.OPENAI_API_KEY))
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MathSolverApi::class.java)
    }

    single<MathSolverRepository> { MathSolverRepositoryImpl(get(), get()) }

    single<ImageCapture> { AndroidImageCapture(androidContext()) }

    viewModel { PhotoSolverViewModel(get(), get()) }
}
