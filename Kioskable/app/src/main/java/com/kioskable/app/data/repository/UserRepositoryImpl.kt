package com.kioskable.app.data.repository

import android.content.SharedPreferences
import com.kioskable.app.data.local.db.dao.UserDao
import com.kioskable.app.data.local.db.entity.UserEntity
import com.kioskable.app.data.remote.api.ApiService
import com.kioskable.app.data.remote.dto.LoginRequest
import com.kioskable.app.domain.model.Result
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val sharedPreferences: SharedPreferences
) : UserRepository {
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }
    
    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            
            // Save token and user info
            sharedPreferences.edit()
                .putString(KEY_AUTH_TOKEN, response.token)
                .putString(KEY_USER_ID, response.user.id)
                .apply()
            
            // Save user to local database
            userDao.insertUser(response.user.toEntity())
            
            Result.Success(response.token)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.message}")
        }
    }
    
    override suspend fun getCurrentUser(): Result<UserEntity?> {
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        
        return if (userId != null) {
            try {
                // First try to fetch from local database
                val localUser = userDao.getUserByEmail(userId)
                
                if (localUser != null) {
                    Result.Success(localUser)
                } else {
                    // If not in local DB, try to get from API
                    val token = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
                    
                    if (token != null) {
                        try {
                            val userDto = apiService.getCurrentUser("Bearer $token")
                            val userEntity = userDto.toEntity()
                            userDao.insertUser(userEntity)
                            Result.Success(userEntity)
                        } catch (e: Exception) {
                            Result.Error("Failed to fetch user: ${e.message}")
                        }
                    } else {
                        Result.Error("Authentication token not found")
                    }
                }
            } catch (e: Exception) {
                Result.Error("Error retrieving user: ${e.message}")
            }
        } else {
            Result.Success(null) // No user ID stored, not logged in
        }
    }
    
    override fun observeCurrentUser(): Flow<UserEntity?> {
        val userId = sharedPreferences.getString(KEY_USER_ID, null) ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.getUserById(userId)
    }
    
    override suspend fun logout() {
        // Clear auth token and user ID
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_ID)
            .apply()
        
        // Clear user from local database
        userDao.deleteAllUsers()
    }
} 