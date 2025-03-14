package com.kioskable.api.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    
    @Indexed
    val businessId: String? = null,  // null for system admin
    
    val firstName: String,
    
    val lastName: String,
    
    @Indexed(unique = true)
    val email: String,
    
    val password: String,  // Hashed
    
    val phoneNumber: String? = null,
    
    val profilePicture: String? = null,  // URL to profile picture
    
    val role: Role,
    
    val permissions: List<String> = listOf(),
    
    val isEnabled: Boolean = true,
    
    val lastLogin: LocalDateTime? = null,
    
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class Role {
    SYSTEM_ADMIN,       // Has access to all businesses
    BUSINESS_ADMIN,     // Has full access to their business
    CONTENT_MANAGER,    // Can manage content and playlists
    DEVICE_MANAGER,     // Can manage devices
    VIEWER              // Read-only access
} 