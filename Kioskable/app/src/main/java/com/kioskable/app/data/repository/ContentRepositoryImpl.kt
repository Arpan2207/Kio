package com.kioskable.app.data.repository

import android.content.SharedPreferences
import com.kioskable.app.data.local.db.dao.ContentDao
import com.kioskable.app.data.local.db.entity.ContentEntity
import com.kioskable.app.data.remote.api.ApiService
import com.kioskable.app.domain.model.Result
import com.kioskable.app.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ContentRepositoryImpl(
    private val apiService: ApiService,
    private val contentDao: ContentDao,
    private val sharedPreferences: SharedPreferences
) : ContentRepository {
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
    
    override suspend fun getContent(page: Int, size: Int): Result<List<ContentEntity>> {
        return try {
            // Try to get from local database first
            val localContent = contentDao.getAllContent().firstOrNull() ?: emptyList()
            
            // If local database is empty, fetch from API
            if (localContent.isEmpty()) {
                refreshContent(page, size)
                val updatedContent = contentDao.getAllContent().firstOrNull() ?: emptyList()
                Result.Success(updatedContent)
            } else {
                Result.Success(localContent)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error("Failed to get content: ${e.message}")
        }
    }
    
    override fun observeAllContent(): Flow<List<ContentEntity>> {
        return contentDao.getAllContent()
    }
    
    override fun observeActiveContent(): Flow<List<ContentEntity>> {
        return contentDao.getActiveContent()
    }
    
    override suspend fun getContentById(contentId: String): Result<ContentEntity?> {
        return try {
            val content = contentDao.getContentById(contentId)
            Result.Success(content)
        } catch (e: Exception) {
            Result.Error("Failed to get content: ${e.message}")
        }
    }
    
    override suspend fun refreshContent(page: Int, size: Int): Result<Unit> {
        return try {
            val token = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
                ?: return Result.Error("Authentication token not found")
            
            val contentResponse = apiService.getContent("Bearer $token", page, size)
            
            // Map to entities and save to database
            val contentEntities = contentResponse.content.map { it.toEntity() }
            contentDao.insertAllContent(contentEntities)
            
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Failed to refresh content: ${e.message}")
        }
    }
    
    override suspend fun clearContentCache(): Result<Unit> {
        return try {
            contentDao.deleteAllContent()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear content cache: ${e.message}")
        }
    }
} 