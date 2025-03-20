package com.kioskable.app.domain.repository

import com.kioskable.app.data.local.db.entity.ContentEntity
import com.kioskable.app.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    suspend fun getContent(page: Int, size: Int): Result<List<ContentEntity>>
    
    fun observeAllContent(): Flow<List<ContentEntity>>
    
    fun observeActiveContent(): Flow<List<ContentEntity>>
    
    suspend fun getContentById(contentId: String): Result<ContentEntity?>
    
    suspend fun refreshContent(page: Int, size: Int): Result<Unit>
    
    suspend fun clearContentCache(): Result<Unit>
} 