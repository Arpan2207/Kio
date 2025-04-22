package com.kioskable.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kioskable.app.data.repository.UserRepository
import com.kioskable.app.domain.model.Result
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult
    
    fun login(email: String, password: String) {
        _loginResult.value = Result.Loading
        
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            _loginResult.value = result
        }
    }
} 