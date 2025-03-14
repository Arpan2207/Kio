package com.kioskable.api.dto.response

import com.kioskable.api.model.Role
import com.kioskable.api.model.User
import java.time.LocalDateTime

data class UserDto(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: Role,
    val businessId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastLogin: LocalDateTime?
) {
    companion object {
        fun fromEntity(user: User): UserDto {
            return UserDto(
                id = user.id ?: "",
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                role = user.role,
                businessId = user.businessId,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                lastLogin = user.lastLogin
            )
        }
    }
} 