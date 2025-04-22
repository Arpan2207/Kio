package com.kioskable.app.data.repository

import android.content.SharedPreferences
import com.kioskable.app.data.local.db.dao.ContentDao
import com.kioskable.app.data.local.db.entity.ContentEntity
import com.kioskable.app.data.remote.api.ApiService
import com.kioskable.app.data.remote.api.ContentApiService
import com.kioskable.app.data.remote.dto.toContent
import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.model.Result
import com.kioskable.app.domain.repository.ContentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    private val contentApiService: ContentApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiService: ApiService,
    private val contentDao: ContentDao,
    private val sharedPreferences: SharedPreferences
) : ContentRepository {
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
    
    override suspend fun getAllContent(page: Int, size: Int): Result<List<Content>> = withContext(ioDispatcher) {
        try {
            val response = contentApiService.getAllContent(page, size)
            Result.Success(response.content.map { it.toContent() })
        } catch (e: HttpException) {
            Result.Error(e.message ?: "HTTP Error")
        } catch (e: IOException) {
            Result.Error("Network Error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Unknown Error: ${e.message}")
        }
    }
    
    override suspend fun getActiveContent(page: Int, size: Int): Result<List<Content>> = withContext(ioDispatcher) {
        try {
            val response = contentApiService.getActiveContent(page, size)
            Result.Success(response.content.map { it.toContent() })
        } catch (e: HttpException) {
            Result.Error(e.message ?: "HTTP Error")
        } catch (e: IOException) {
            Result.Error("Network Error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Unknown Error: ${e.message}")
        }
    }
    
    override suspend fun getScheduledContent(page: Int, size: Int): Result<List<Content>> = withContext(ioDispatcher) {
        try {
            val response = contentApiService.getScheduledContent(page, size)
            Result.Success(response.content.map { it.toContent() })
        } catch (e: HttpException) {
            Result.Error(e.message ?: "HTTP Error")
        } catch (e: IOException) {
            Result.Error("Network Error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Unknown Error: ${e.message}")
        }
    }
    
    override suspend fun getContentById(id: String): Result<Content> = withContext(ioDispatcher) {
        try {
            val response = contentApiService.getContentById(id)
            Result.Success(response.toContent())
        } catch (e: HttpException) {
            Result.Error(e.message ?: "HTTP Error")
        } catch (e: IOException) {
            Result.Error("Network Error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Unknown Error: ${e.message}")
        }
    }
    
    override fun observeDisplayContent(): Flow<List<Content>> = flow {
        try {
            val response = contentApiService.getScheduledContent(0, 100)
            emit(response.content.map { it.toContent() })
        } catch (e: Exception) {
            // In case of error, emit empty list
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)
    
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
    
    override suspend fun refreshContent(page: Int, size: Int): Result<Unit> {
        return try {
            val token = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
                ?: return Result.Error("Authentication token not found")
            
            val contentResponse = apiService.getContent("Bearer $token", page, size)
            
            // Map to entities and save to database
            val contentEntities = contentResponse.content.map { it.toEntity() }
            contentDao.insertAllContent(contentEntities)
            
            Result.Success(Unit)
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
} 