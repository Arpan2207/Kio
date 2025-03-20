package com.kioskable.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kioskable.app.data.local.db.converter.StringListConverter

@Entity(tableName = "devices")
@TypeConverters(StringListConverter::class)
data class DeviceEntity(
    @PrimaryKey
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
    val location: String?,
    val createdAt: String,
    val updatedAt: String
) 