package com.kioskable.api.repository

import com.kioskable.api.model.Content
import com.kioskable.api.model.ContentType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ContentRepository : MongoRepository<Content, String> {
    fun findByBusinessId(businessId: String, pageable: Pageable): Page<Content>
    fun findByBusinessIdAndActive(businessId: String, active: Boolean, pageable: Pageable): Page<Content>
    fun findByBusinessIdAndType(businessId: String, type: ContentType, pageable: Pageable): Page<Content>
    fun findByBusinessIdAndActiveAndScheduleStartBeforeAndScheduleEndAfter(
        businessId: String,
        active: Boolean,
        currentTime: LocalDateTime,
        currentTime2: LocalDateTime,
        pageable: Pageable
    ): Page<Content>
    fun countByBusinessId(businessId: String): Long
} 