package com.kioskable.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kioskable.app.data.local.db.entity.ContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContent(content: List<ContentEntity>)
    
    @Query("SELECT * FROM content ORDER BY `order` ASC")
    fun getAllContent(): Flow<List<ContentEntity>>
    
    @Query("SELECT * FROM content WHERE active = 1 ORDER BY `order` ASC")
    fun getActiveContent(): Flow<List<ContentEntity>>
    
    @Query("SELECT * FROM content WHERE id = :contentId")
    suspend fun getContentById(contentId: String): ContentEntity?
    
    @Query("DELETE FROM content WHERE id = :contentId")
    suspend fun deleteContent(contentId: String)
    
    @Query("DELETE FROM content")
    suspend fun deleteAllContent()
} 