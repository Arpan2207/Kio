package com.kioskable.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kioskable.app.data.local.db.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)
    
    @Query("SELECT * FROM devices")
    fun getAllDevices(): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceEntity?
    
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun observeDeviceById(deviceId: String): Flow<DeviceEntity?>
    
    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDevice(deviceId: String)
    
    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()
} 