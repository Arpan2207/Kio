package com.kioskable.api.dto.response

import com.kioskable.api.model.Content
import com.kioskable.api.model.ContentType
import java.time.LocalDateTime

data class ContentResponse(
    val id: String,
    val title: String,
    val type: ContentType,
    val content: ContentDataResponse,
    val duration: Int?,
    val order: Int,
    val active: Boolean,
    val scheduleStart: LocalDateTime?,
    val scheduleEnd: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val createdBy: String?
) {
    companion object {
        fun fromEntity(content: Content): ContentResponse {
            return ContentResponse(
                id = content.id!!,
                title = content.title,
                type = content.type,
                content = ContentDataResponse(
                    url = content.content.url,
                    text = content.content.text,
                    link = content.content.link
                ),
                duration = content.duration,
                order = content.order,
                active = content.active,
                scheduleStart = content.scheduleStart,
                scheduleEnd = content.scheduleEnd,
                createdAt = content.createdAt,
                updatedAt = content.updatedAt,
                createdBy = content.createdBy
            )
        }
    }
}

data class ContentDataResponse(
    val url: String?,
    val text: String?,
    val link: String?
)

data class PagedContentResponse(
    val content: List<ContentResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
) 