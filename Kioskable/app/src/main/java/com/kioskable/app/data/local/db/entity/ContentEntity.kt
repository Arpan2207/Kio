package com.kioskable.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content")
data class ContentEntity(
    @PrimaryKey
    val id: String,
    val businessId: String,
    val title: String,
    val type: String,
    val url: String?,
    val text: String?,
    val link: String?,
    val duration: Int,
    val order: Int,
    val active: Boolean,
    val scheduleStart: String?,
    val scheduleEnd: String?,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?
) 