package com.kioskable.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kioskable.app.data.local.db.converter.DateConverter
import com.kioskable.app.data.local.db.dao.UserDao
import com.kioskable.app.data.local.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class
        // Add more entities as they are created
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    // Add more DAOs as needed
} 