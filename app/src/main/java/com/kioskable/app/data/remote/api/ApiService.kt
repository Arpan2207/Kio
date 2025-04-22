package com.kioskable.app.data.remote.api

import com.kioskable.app.data.remote.dto.LoginRequest
import com.kioskable.app.data.remote.dto.LoginResponse
import com.kioskable.app.data.remote.dto.MessageResponse
import com.kioskable.app.data.remote.dto.PasswordResetRequest
import com.kioskable.app.data.remote.dto.RegisterRequest
import com.kioskable.app.data.remote.dto.RegisterResponse
import com.kioskable.app.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse
    
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body passwordResetRequest: PasswordResetRequest): MessageResponse
    
    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): UserDto
    
    // Device endpoints
    @GET("devices")
    suspend fun getDevices(@Header("Authorization") token: String): List<com.kioskable.app.data.remote.dto.DeviceDto>
    
    @GET("devices/{id}")
    suspend fun getDeviceById(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") deviceId: String
    ): com.kioskable.app.data.remote.dto.DeviceDto
    
    // Content endpoints
    @GET("content")
    suspend fun getContent(
        @Header("Authorization") token: String,
        @retrofit2.http.Query("page") page: Int,
        @retrofit2.http.Query("size") size: Int
    ): com.kioskable.app.data.remote.dto.PagedResponse<com.kioskable.app.data.remote.dto.ContentDto>
    
    // Add more API endpoints as needed
} 