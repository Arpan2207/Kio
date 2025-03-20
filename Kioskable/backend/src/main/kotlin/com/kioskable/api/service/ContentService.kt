package com.kioskable.api.service

import com.kioskable.api.exception.ResourceNotFoundException
import com.kioskable.api.model.Content
import com.kioskable.api.model.ContentData
import com.kioskable.api.model.ContentType
import com.kioskable.api.repository.ContentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ContentService(
    private val contentRepository: ContentRepository,
    private val fileStorageService: FileStorageService
) {
    
    /**
     * Get content for a business with pagination
     */
    fun getContentByBusinessId(businessId: String, pageable: Pageable): Page<Content> {
        return contentRepository.findByBusinessId(businessId, pageable)
    }
    
    /**
     * Get active content for a business with pagination
     */
    fun getActiveContentByBusinessId(businessId: String, pageable: Pageable): Page<Content> {
        return contentRepository.findByBusinessIdAndActive(businessId, true, pageable)
    }
    
    /**
     * Get content by its ID
     */
    fun getContentById(id: String): Content {
        return contentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Content with id $id not found") }
    }
    
    /**
     * Get currently scheduled content for display
     */
    fun getScheduledContent(businessId: String, pageable: Pageable): Page<Content> {
        val now = LocalDateTime.now()
        return contentRepository.findByBusinessIdAndActiveAndScheduleStartBeforeAndScheduleEndAfter(
            businessId, true, now, now, pageable
        )
    }
    
    /**
     * Create new content
     */
    fun createContent(content: Content): Content {
        return contentRepository.save(content)
    }
    
    /**
     * Update existing content
     */
    fun updateContent(id: String, updatedContent: Content): Content {
        val existingContent = getContentById(id)
        
        // Update fields, preserving created date and ID
        val contentToSave = updatedContent.copy(
            id = existingContent.id,
            createdAt = existingContent.createdAt,
            updatedAt = LocalDateTime.now()
        )
        
        return contentRepository.save(contentToSave)
    }
    
    /**
     * Delete content by ID
     */
    fun deleteContent(id: String) {
        val content = getContentById(id)
        contentRepository.delete(content)
        
        // If it's an image or video, delete the file as well
        if (content.type == ContentType.IMAGE || content.type == ContentType.VIDEO) {
            content.content.url?.let { fileStorageService.deleteFile(it) }
        }
    }
    
    /**
     * Upload a file for content (image or video)
     */
    fun uploadContentFile(businessId: String, fileBytes: ByteArray, fileName: String, contentType: String): String {
        val fileDirectory = "content/$businessId"
        return fileStorageService.storeFile(fileBytes, fileName, fileDirectory, contentType)
    }
    
    /**
     * Activate or deactivate content
     */
    fun setContentActiveStatus(id: String, active: Boolean): Content {
        val content = getContentById(id)
        val updatedContent = content.copy(
            active = active,
            updatedAt = LocalDateTime.now()
        )
        return contentRepository.save(updatedContent)
    }
    
    /**
     * Reorder content items
     */
    fun reorderContent(contentIds: List<String>): List<Content> {
        val updatedContentList = mutableListOf<Content>()
        
        contentIds.forEachIndexed { index, contentId ->
            val content = getContentById(contentId)
            val updatedContent = content.copy(
                order = index,
                updatedAt = LocalDateTime.now()
            )
            updatedContentList.add(contentRepository.save(updatedContent))
        }
        
        return updatedContentList
    }
} 