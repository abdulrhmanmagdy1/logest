package com.edham.logistics.data.local.database

import android.content.Context
import androidx.room.*
import com.edham.logistics.data.local.database.dao.MaintenanceDao
import com.edham.logistics.data.local.database.dao.TechnicianDao
import com.edham.logistics.data.local.database.dao.RepairDao
import com.edham.logistics.data.local.database.dao.SparePartsDao
import com.edham.logistics.data.local.database.dao.DowntimeDao
import com.edham.logistics.data.local.database.dao.UserDao
import com.edham.logistics.data.local.dao.VehicleDao
import com.edham.logistics.data.local.entity.*

@Database(
    entities = [
        MaintenanceEntity::class,
        VehicleEntity::class,
        TechnicianEntity::class,
        TripEntity::class,
        InvoiceEntity::class,
        LocationEntity::class,
        DocumentEntity::class,
        RepairEntity::class,
        SparePartEntity::class,
        DowntimeEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class EdhamDatabase : RoomDatabase() {
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun technicianDao(): TechnicianDao
    abstract fun repairDao(): RepairDao
    abstract fun sparePartsDao(): SparePartsDao
    abstract fun downtimeDao(): DowntimeDao
    
    // Simulated workshopDao for compatibility if needed
    fun workshopDao() = object {
        fun getWorkshop() = object {
            val totalBays = 5
            val workingHours = 8
        }
    }
    
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
