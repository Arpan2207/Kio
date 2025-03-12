package com.kioskable.app.data.remote.dto

import com.kioskable.app.data.local.db.entity.UserEntity
import java.util.Date

data class UserDto(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: String,
    val businessId: String?,
    val createdAt: String,
    val updatedAt: String,
    val lastLogin: String?
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            role = role,
            businessId = businessId,
            createdAt = parseDate(createdAt),
            updatedAt = parseDate(updatedAt),
            lastLogin = lastLogin?.let { parseDate(it) }
        )
    }
    
    private fun parseDate(dateString: String): Date {
        // Simple parsing for now - would use a proper formatter in production
        return Date()
    }
} 