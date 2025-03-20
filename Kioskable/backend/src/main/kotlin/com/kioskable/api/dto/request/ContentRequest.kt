package com.kioskable.api.dto.request

import com.kioskable.api.model.Content
import com.kioskable.api.model.ContentData
import com.kioskable.api.model.ContentType
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ContentRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,
    
    @field:NotNull(message = "Content type is required")
    val type: ContentType,
    
    val content: ContentDataRequest,
    
    val duration: Int? = null,
    
    val order: Int = 0,
    
    val active: Boolean = true,
    
    val scheduleStart: LocalDateTime? = null,
    
    val scheduleEnd: LocalDateTime? = null
) {
    fun toEntity(businessId: String, createdBy: String?): Content {
        return Content(
            businessId = businessId,
            title = title,
            type = type,
            content = ContentData(
                url = content.url,
                text = content.text,
                link = content.link
            ),
            duration = duration,
            order = order,
            active = active,
            scheduleStart = scheduleStart,
            scheduleEnd = scheduleEnd,
            createdBy = createdBy
        )
    }
}

data class ContentDataRequest(
    val url: String? = null,
    val text: String? = null,
    val link: String? = null
) 