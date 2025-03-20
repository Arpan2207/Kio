package com.kioskable.app.data.remote.dto

import com.kioskable.app.data.local.db.entity.ContentEntity

data class ContentDto(
    val id: String,
    val businessId: String,
    val title: String,
    val type: String,
    val content: ContentDataDto,
    val duration: Int?,
    val order: Int,
    val active: Boolean,
    val scheduleStart: String?,
    val scheduleEnd: String?,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?
) {
    fun toEntity(): ContentEntity {
        return ContentEntity(
            id = id,
            businessId = businessId,
            title = title,
            type = type,
            url = content.url,
            text = content.text,
            link = content.link,
            duration = duration ?: 0,
            order = order,
            active = active,
            scheduleStart = scheduleStart,
            scheduleEnd = scheduleEnd,
            createdAt = createdAt,
            updatedAt = updatedAt,
            createdBy = createdBy
        )
    }
}

data class ContentDataDto(
    val url: String?,
    val text: String?,
    val link: String?
) 