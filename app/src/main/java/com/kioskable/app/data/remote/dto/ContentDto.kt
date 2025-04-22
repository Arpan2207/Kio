package com.kioskable.app.data.remote.dto

import com.kioskable.app.data.local.db.entity.ContentEntity
import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.model.ContentData
import com.kioskable.app.domain.model.ContentType
import java.time.LocalDateTime

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

data class PagedContentResponse(
    val content: List<ContentDto>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

/**
 * Extension function to convert ContentDto to domain Content model
 */
fun ContentDto.toContent(): Content {
    return Content(
        id = id,
        title = title,
        type = ContentType.valueOf(type),
        content = ContentData(
            url = content.url,
            text = content.text,
            link = content.link
        ),
        duration = duration,
        order = order,
        active = active,
        scheduleStart = scheduleStart?.let { LocalDateTime.parse(it) },
        scheduleEnd = scheduleEnd?.let { LocalDateTime.parse(it) },
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt)
    )
} 