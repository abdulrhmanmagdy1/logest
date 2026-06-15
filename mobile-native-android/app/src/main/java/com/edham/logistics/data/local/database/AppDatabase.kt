package com.edham.logistics.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.edham.logistics.data.local.database.dao.RouteDao
import com.edham.logistics.data.local.entity.RouteEntity
import com.edham.logistics.data.local.entity.RouteStopEntity

@Database(
    entities = [
        RouteEntity::class,
        RouteStopEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao
}
