package com.kioskable.app.application

import android.app.Application
import com.kioskable.app.application.di.appModule
import com.kioskable.app.application.di.networkModule
import com.kioskable.app.application.di.repositoryModule
import com.kioskable.app.application.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin for dependency injection
        startKoin {
            androidLogger(Level.ERROR) // Use ERROR level to avoid crash on Kotlin 1.4
            androidContext(this@App)
            modules(
                appModule,
                networkModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
} 