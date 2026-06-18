package com.farmacox.farmacode.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://farmacode-production-c60c.up.railway.app/"

    private const val API_KEY = "farmacode-secret-2026"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val builder = chain.request().newBuilder()
                .addHeader("X-Api-Key", API_KEY)
            com.farmacox.farmacode.viewmodel.UserSession.userId?.let {
                builder.addHeader("X-User-Id", it.toString())
            }
            chain.proceed(builder.build())
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val busquedaService: BusquedaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BusquedaApiService::class.java)
    }

    val apiService: FarmaCodeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FarmaCodeApiService::class.java)
    }
}
