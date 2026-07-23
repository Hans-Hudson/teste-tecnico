package com.hansbraga.testetecnico.mathsolver.di

import com.hansbraga.testetecnico.BuildConfig
import com.hansbraga.testetecnico.mathsolver.data.MathSolverApi
import com.hansbraga.testetecnico.mathsolver.data.MathSolverRepositoryImpl
import com.hansbraga.testetecnico.mathsolver.domain.MathSolverRepository
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverViewModel
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val OPENAI_BASE_URL = "https://api.openai.com/v1/"

val mathSolverModule = module {
    single { Json { ignoreUnknownKeys = true } }

    single {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                .build()
            chain.proceed(request)
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
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

    viewModel { PhotoSolverViewModel(get()) }
}
