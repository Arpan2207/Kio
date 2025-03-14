package com.kioskable.api.repository

import com.kioskable.api.model.Playlist
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PlaylistRepository : MongoRepository<Playlist, String> {
    fun findByBusinessId(businessId: String, pageable: Pageable): Page<Playlist>
    fun findByBusinessIdAndActive(businessId: String, active: Boolean, pageable: Pageable): Page<Playlist>
    fun findByDevicesContaining(deviceId: String): List<Playlist>
    fun findByDevicesContainingAndActive(deviceId: String, active: Boolean): List<Playlist>
    fun findByBusinessIdAndActiveAndScheduleStartBeforeAndScheduleEndAfter(
        businessId: String,
        active: Boolean,
        currentTime: LocalDateTime,
        currentTime2: LocalDateTime,
        pageable: Pageable
    ): Page<Playlist>
} 