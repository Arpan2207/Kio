package com.kioskable.api.repository

import com.kioskable.api.model.Device
import com.kioskable.api.model.DeviceStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DeviceRepository : MongoRepository<Device, String> {
    fun findByBusinessId(businessId: String): List<Device>
    fun findByBusinessIdAndStatus(businessId: String, status: DeviceStatus): List<Device>
    fun findByDeviceId(deviceId: String): Device?
    fun countByBusinessId(businessId: String): Long
    fun findByLastPingBefore(date: LocalDateTime): List<Device>
    fun findByBusinessIdAndLastPingBefore(businessId: String, date: LocalDateTime): List<Device>
} 