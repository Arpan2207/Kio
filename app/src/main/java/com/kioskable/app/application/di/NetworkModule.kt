package com.kioskable.app.application.di

import com.kioskable.app.data.remote.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    // HTTP Client
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(get<AppConstants>().TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(get<AppConstants>().TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(get<AppConstants>().TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl(get<AppConstants>().BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API Service
    single { get<Retrofit>().create(ApiService::class.java) }
} 