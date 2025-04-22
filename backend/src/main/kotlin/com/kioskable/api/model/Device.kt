package com.kioskable.api.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "devices")
data class Device(
    @Id
    val id: String? = null,
    
    @Indexed
    val businessId: String,
    
    val name: String,
    
    @Indexed(unique = true)
    val deviceId: String, // Unique device identifier
    
    var status: DeviceStatus = DeviceStatus.INACTIVE,
    
    var lastPing: LocalDateTime? = null,
    
    var installedVersion: String? = null,
    
    val osVersion: String? = null,
    
    val screenSize: String? = null,
    
    val activeModules: List<String> = listOf(),
    
    val location: String? = null, // Where device is placed, e.g., "Main Entrance"
    
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DeviceStatus {
    ACTIVE, INACTIVE, MAINTENANCE
} 