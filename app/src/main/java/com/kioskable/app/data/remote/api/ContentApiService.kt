package com.kioskable.app.data.remote.api

import com.kioskable.app.data.remote.dto.ContentDto
import com.kioskable.app.data.remote.dto.PagedContentResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ContentApiService {
    
    @GET("content")
    suspend fun getAllContent(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "order"
    ): PagedContentResponse
    
    @GET("content/active")
    suspend fun getActiveContent(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "order"
    ): PagedContentResponse
    
    @GET("content/scheduled")
    suspend fun getScheduledContent(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "order"
    ): PagedContentResponse
    
    @GET("content/{id}")
    suspend fun getContentById(@Path("id") id: String): ContentDto
} 