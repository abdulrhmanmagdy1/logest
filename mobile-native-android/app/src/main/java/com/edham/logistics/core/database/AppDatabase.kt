package com.edham.logistics.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edham.logistics.core.database.converters.DateConverters
import com.edham.logistics.data.local.dao.DriverFeatureDao
import com.edham.logistics.data.local.dao.InvoiceDao
import com.edham.logistics.data.local.dao.ShipmentDao
import com.edham.logistics.data.local.database.dao.DocumentDao
import com.edham.logistics.data.local.database.dao.DriverDao
import com.edham.logistics.data.local.database.dao.LocationDao
import com.edham.logistics.data.local.dao.VehicleDao
import com.edham.logistics.data.local.entity.*
import com.edham.logistics.data.local.database.entities.ShipmentEntity

@Database(
    entities = [
        ShipmentEntity::class,
        DriverEntity::class,
        VehicleEntity::class,
        InvoiceEntity::class,
        LocationEntity::class,
        DocumentEntity::class,
        TripEntity::class,
        WaypointEntity::class,
        SurveyAnswerEntity::class,
        AttachmentEntity::class,
        LocationCacheEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shipmentDao(): ShipmentDao
    abstract fun driverDao(): DriverDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun locationDao(): LocationDao
    abstract fun documentDao(): DocumentDao
    abstract fun driverFeatureDao(): DriverFeatureDao
}
