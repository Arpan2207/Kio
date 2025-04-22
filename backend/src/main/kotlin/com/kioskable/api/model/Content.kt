package com.kioskable.api.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "content")
data class Content(
    @Id
    val id: String? = null,
    
    @Indexed
    val businessId: String,
    
    val title: String,
    
    val type: ContentType,
    
    val content: ContentData,
    
    val duration: Int? = null, // In seconds
    
    val order: Int = 0,
    
    val active: Boolean = true,
    
    val scheduleStart: LocalDateTime? = null, // Optional scheduling
    
    val scheduleEnd: LocalDateTime? = null,
    
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    val createdBy: String? = null // Reference to User
)

enum class ContentType {
    IMAGE, VIDEO, TEXT, WEB_LINK
}

data class ContentData(
    val url: String? = null, // For images/videos
    val text: String? = null, // For text slides
    val link: String? = null // For web links
) 