package com.kioskable.app.data.remote.api

import com.kioskable.app.data.remote.dto.LoginRequest
import com.kioskable.app.data.remote.dto.LoginResponse
import com.kioskable.app.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    
    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): UserDto
    
    // Add more API endpoints as needed
} 