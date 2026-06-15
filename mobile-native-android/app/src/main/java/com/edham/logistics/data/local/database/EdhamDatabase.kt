package com.edham.logistics.data.local.database

import android.content.Context
import androidx.room.*
import com.edham.logistics.data.local.database.dao.MaintenanceDao
import com.edham.logistics.data.local.database.dao.UserDao
import com.edham.logistics.data.local.database.dao.VehicleDao
import com.edham.logistics.data.local.entity.VehicleEntity

@Database(
    entities = [
        MaintenanceEntity::class,
        VehicleEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class EdhamDatabase : RoomDatabase() {
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun vehicleDao(): VehicleDao
    
    companion object {
        @Volatile
        private var INSTANCE: EdhamDatabase? = null
        
        fun getDatabase(context: Context): EdhamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EdhamDatabase::class.java,
                    "edham_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
