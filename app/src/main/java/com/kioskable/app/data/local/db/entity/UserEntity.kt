package com.kioskable.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: String,
    val businessId: String?,
    val createdAt: Date,
    val updatedAt: Date,
    val lastLogin: Date?
) 