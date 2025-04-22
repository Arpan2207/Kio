package com.kioskable.app.data.remote.dto

import com.kioskable.app.data.local.db.entity.DeviceEntity
import java.time.LocalDateTime

data class DeviceDto(
    val id: String,
    val businessId: String,
    val name: String,
    val deviceId: String,
    val status: String,
    val lastPing: String?,
    val installedVersion: String?,
    val osVersion: String?,
    val screenSize: String?,
    val activeModules: List<String>,
    val location: LocationDto?,
    val createdAt: String,
    val updatedAt: String
) {
    fun toEntity(): DeviceEntity {
        return DeviceEntity(
            id = id,
            businessId = businessId,
            name = name,
            deviceId = deviceId,
            status = status,
            lastPing = lastPing,
            installedVersion = installedVersion,
            osVersion = osVersion,
            screenSize = screenSize,
            activeModules = activeModules,
            location = location?.let { "${it.latitude},${it.longitude}" },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val address: String?
) 