package com.kioskable.app.application.di

import android.content.Context
import androidx.room.Room
import com.kioskable.app.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "kioskable-db"
        ).fallbackToDestructiveMigration().build()
    }
    
    // Provide DAOs
    single { get<AppDatabase>().userDao() }
    
    // SharedPreferences
    single {
        androidContext().getSharedPreferences("kioskable_prefs", Context.MODE_PRIVATE)
    }
    
    // AppConstants
    single { AppConstants() }
}

class AppConstants {
    val BASE_URL = "https://api.kioskable.com/" // This will change to the actual API URL
    val TIMEOUT = 30L // Timeout in seconds for network calls
    val DATABASE_VERSION = 1
} 