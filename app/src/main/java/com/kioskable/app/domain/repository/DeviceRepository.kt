package com.kioskable.app.domain.repository

import com.kioskable.app.data.local.db.entity.DeviceEntity
import com.kioskable.app.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    suspend fun getDevices(): Result<List<DeviceEntity>>
    
    suspend fun getDeviceById(deviceId: String): Result<DeviceEntity?>
    
    fun observeDevices(): Flow<List<DeviceEntity>>
    
    fun observeDeviceById(deviceId: String): Flow<DeviceEntity?>
    
    suspend fun refreshDevices(): Result<Unit>
    
    suspend fun refreshDevice(deviceId: String): Result<Unit>
    
    suspend fun clearDeviceCache(): Result<Unit>
} 