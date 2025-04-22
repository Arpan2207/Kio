package com.kioskable.api.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "playlists")
data class Playlist(
    @Id
    val id: String? = null,
    
    @Indexed
    val businessId: String,
    
    val name: String,
    
    val description: String? = null,
    
    val contentIds: List<String> = listOf(), // References to Content documents
    
    val devices: List<String> = listOf(), // Device IDs assigned to this playlist
    
    val active: Boolean = true,
    
    val scheduleStart: LocalDateTime? = null, // Optional scheduling
    
    val scheduleEnd: LocalDateTime? = null,
    
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    val createdBy: String? = null // Reference to User
) 