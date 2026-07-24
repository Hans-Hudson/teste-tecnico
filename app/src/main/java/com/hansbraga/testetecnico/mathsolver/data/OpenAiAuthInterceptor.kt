package com.hansbraga.testetecnico.mathsolver.data

import okhttp3.Interceptor

fun openAiAuthInterceptor(apiKey: String): Interceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $apiKey")
        .build()
    chain.proceed(request)
}
