package com.kioskable.app.application.di

import com.kioskable.app.data.repository.ContentRepositoryImpl
import com.kioskable.app.data.repository.DeviceRepositoryImpl
import com.kioskable.app.data.repository.UserRepositoryImpl
import com.kioskable.app.domain.repository.ContentRepository
import com.kioskable.app.domain.repository.DeviceRepository
import com.kioskable.app.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    // Authentication repository
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }
    
    // Device repository
    single<DeviceRepository> { DeviceRepositoryImpl(get(), get(), get()) }
    
    // Content repository
    single<ContentRepository> { ContentRepositoryImpl(get(), get(), get()) }
    
    // Add other repositories as needed
} 