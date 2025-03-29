package com.kioskable.app.domain.repository

import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    /**
     * Get all content with pagination
     */
    suspend fun getAllContent(page: Int, size: Int): Result<List<Content>>
    
    /**
     * Get active content with pagination
     */
    suspend fun getActiveContent(page: Int, size: Int): Result<List<Content>>
    
    /**
     * Get currently scheduled content
     */
    suspend fun getScheduledContent(page: Int, size: Int): Result<List<Content>>
    
    /**
     * Get content by ID
     */
    suspend fun getContentById(id: String): Result<Content>
    
    /**
     * Get flow of active and scheduled content for display
     */
    fun observeDisplayContent(): Flow<List<Content>>
} 