package com.kioskable.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "modules")
data class Module(
    @Id
    val id: String? = null,
    
    @Indexed(unique = true)
    val code: String, // Unique code identifier like "display", "forms", "wayfinding"
    
    val name: String,
    
    val description: String,
    
    val version: String,
    
    val requiredPermissions: List<String> = listOf(),
    
    val minimumSubscription: SubscriptionType = SubscriptionType.FREE,
    
    val isCore: Boolean = false, // Is this a core module that cannot be disabled?
    
    val isActive: Boolean = true,
    
    val requiredModules: List<String> = listOf(), // Other module codes this one depends on
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime = LocalDateTime.now()
) 