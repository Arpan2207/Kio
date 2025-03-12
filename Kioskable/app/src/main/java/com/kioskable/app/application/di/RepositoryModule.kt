package com.kioskable.app.application.di

import com.kioskable.app.data.repository.UserRepository
import com.kioskable.app.data.repository.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    // Authentication repository
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    
    // Add other repositories as needed
} 