package com.kioskable.app.domain.model

import java.time.LocalDateTime

data class Content(
    val id: String,
    val title: String,
    val type: ContentType,
    val content: ContentData,
    val duration: Int?,
    val order: Int,
    val active: Boolean,
    val scheduleStart: LocalDateTime?,
    val scheduleEnd: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class ContentData(
    val url: String?,
    val text: String?,
    val link: String?
)

enum class ContentType {
    IMAGE, 
    VIDEO, 
    TEXT, 
    WEB_LINK
} 