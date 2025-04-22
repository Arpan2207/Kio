package com.kioskable.app.application.di

import com.kioskable.app.ui.auth.LoginViewModel
import com.kioskable.app.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Auth ViewModels
    viewModel { LoginViewModel(get()) }
    
    // Main ViewModel
    viewModel { MainViewModel() }
    
    // Add other ViewModels as they are created
} 