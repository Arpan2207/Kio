package com.kioskable.app.data.repository

import android.content.SharedPreferences
import com.kioskable.app.data.local.db.dao.DeviceDao
import com.kioskable.app.data.local.db.entity.DeviceEntity
import com.kioskable.app.data.remote.api.ApiService
import com.kioskable.app.domain.model.Result
import com.kioskable.app.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class DeviceRepositoryImpl(
    private val apiService: ApiService,
    private val deviceDao: DeviceDao,
    private val sharedPreferences: SharedPreferences
) : DeviceRepository {
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
    
    override suspend fun getDevices(): Result<List<DeviceEntity>> {
        return try {
            // First try to get from local database
            val localDevices = deviceDao.getAllDevices().firstOrNull() ?: emptyList()
            
            // If the local database is empty, fetch from API
            if (localDevices.isEmpty()) {
                refreshDevices()
                val updatedDevices = deviceDao.getAllDevices().firstOrNull() ?: emptyList()
                Result.Success(updatedDevices)
            } else {
                Result.Success(localDevices)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error("Failed to get devices: ${e.message}")
        }
    }
    
    override suspend fun getDeviceById(deviceId: String): Result<DeviceEntity?> {
        return try {
            // Try to get from local database
            val localDevice = deviceDao.getDeviceById(deviceId)
            
            // If not found locally, try to fetch from API
            if (localDevice == null) {
                refreshDevice(deviceId)
                val updatedDevice = deviceDao.getDeviceById(deviceId)
                Result.Success(updatedDevice)
            } else {
                Result.Success(localDevice)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error("Failed to get device: ${e.message}")
        }
    }
    
    override fun observeDevices(): Flow<List<DeviceEntity>> {
        return deviceDao.getAllDevices()
    }
    
    override fun observeDeviceById(deviceId: String): Flow<DeviceEntity?> {
        return deviceDao.observeDeviceById(deviceId)
    }
    
    override suspend fun refreshDevices(): Result<Unit> {
        return try {
            val token = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
                ?: return Result.Error("Authentication token not found")
            
            val devices = apiService.getDevices("Bearer $token")
            
            // Map to entities and save to database
            val deviceEntities = devices.map { it.toEntity() }
            deviceDao.insertDevices(deviceEntities)
            
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Failed to refresh devices: ${e.message}")
        }
    }
    
    override suspend fun refreshDevice(deviceId: String): Result<Unit> {
        return try {
            val token = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
                ?: return Result.Error("Authentication token not found")
            
            val device = apiService.getDeviceById("Bearer $token", deviceId)
            
            // Save to database
            deviceDao.insertDevice(device.toEntity())
            
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Failed to refresh device: ${e.message}")
        }
    }
    
    override suspend fun clearDeviceCache(): Result<Unit> {
        return try {
            deviceDao.deleteAllDevices()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear device cache: ${e.message}")
        }
    }
} 