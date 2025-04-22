package com.kioskable.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kioskable.app.data.local.db.converter.DateConverter
import com.kioskable.app.data.local.db.converter.StringListConverter
import com.kioskable.app.data.local.db.dao.ContentDao
import com.kioskable.app.data.local.db.dao.DeviceDao
import com.kioskable.app.data.local.db.dao.UserDao
import com.kioskable.app.data.local.db.entity.ContentEntity
import com.kioskable.app.data.local.db.entity.DeviceEntity
import com.kioskable.app.data.local.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        DeviceEntity::class,
        ContentEntity::class
        // Add more entities as they are created
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deviceDao(): DeviceDao
    abstract fun contentDao(): ContentDao
    // Add more DAOs as needed
} 