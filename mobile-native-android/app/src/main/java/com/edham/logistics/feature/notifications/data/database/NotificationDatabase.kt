package com.edham.logistics.feature.notifications.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.edham.logistics.feature.notifications.data.database.dao.NotificationDao
import com.edham.logistics.feature.notifications.data.database.dao.NotificationSettingsDao
import com.edham.logistics.feature.notifications.data.database.entity.NotificationEntity
import com.edham.logistics.feature.notifications.data.database.entity.NotificationSettingsEntity
import com.edham.logistics.feature.notifications.data.database.converter.NotificationConverters

@Database(
    entities = [
        NotificationEntity::class,
        NotificationSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(NotificationConverters::class)
abstract class NotificationDatabase : RoomDatabase() {
    
    abstract fun notificationDao(): NotificationDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
    
    companion object {
        const val DATABASE_NAME = "notification_database"
    }
}

// Migrations
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns or tables for future versions
    }
}
