package com.kioskable.app.data.repository

import com.kioskable.app.data.local.db.entity.UserEntity
import com.kioskable.app.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, password: String): Result<String>
    
    suspend fun getCurrentUser(): Result<UserEntity?>
    
    fun observeCurrentUser(): Flow<UserEntity?>
    
    suspend fun logout()
} 