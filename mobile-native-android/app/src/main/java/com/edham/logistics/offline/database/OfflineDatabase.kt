package com.edham.logistics.offline.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Room Database for Offline-First Architecture
 * Primary data source when offline, syncs with server when online
 */

@Entity(tableName = "shipments")
data class ShipmentEntity(
    @PrimaryKey val id: String,
    val trackingNumber: String,
    val customerId: String,
    val driverId: String?,
    val origin: String,
    val destination: String,
    val status: String,
    val priority: String,
    val weight: Double,
    val dimensions: String,
    val specialInstructions: String?,
    val estimatedDelivery: Long,
    val actualDelivery: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: String = "SYNCED",
    val lastSyncTime: Long = System.currentTimeMillis(),
    val isOffline: Boolean = false
)

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val licenseNumber: String,
    val vehicleId: String?,
    val currentLocation: String?,
    val status: String,
    val rating: Double,
    val totalDeliveries: Int,
    val isAvailable: Boolean,
    val syncStatus: String = "SYNCED",
    val lastSyncTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val licensePlate: String,
    val make: String,
    val model: String,
    val year: Int,
    val type: String,
    val capacity: Double,
    val fuelType: String,
    val currentMileage: Double,
    val status: String,
    val driverId: String?,
    val lastMaintenance: Long?,
    val nextMaintenance: Long?,
    val syncStatus: String = "SYNCED",
    val lastSyncTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: String,
    val shipmentId: String,
    val driverId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float,
    val speed: Float?,
    val bearing: Float?,
    val syncStatus: String = "PENDING",
    val lastSyncTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey val id: String,
    val operationType: String,
    val entityType: String,
    val entityId: String,
    val data: String, // JSON representation of the data
    val timestamp: Long,
    val status: String, // PENDING, IN_PROGRESS, COMPLETED, FAILED
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val lastError: String?,
    val nextRetryTime: Long = 0
)

@Entity(tableName = "cached_maps_data")
data class CachedMapsDataEntity(
    @PrimaryKey val cacheKey: String,
    val data: String, // JSON or base64 encoded data
    val dataType: String, // TILES, ROUTE, GEOCODING, PLACES
    val bounds: String?, // For map tiles
    val timestamp: Long,
    val expiryTime: Long,
    val size: Long, // Size in bytes
    val accessCount: Int = 0,
    val lastAccessed: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_settings")
data class OfflineSettingsEntity(
    @PrimaryKey val key: String,
    val value: String,
    val timestamp: Long = System.currentTimeMillis()
)

// DAOs
@Dao
interface ShipmentDao {
    @Query("SELECT * FROM shipments ORDER BY createdAt DESC")
    fun getAllShipments(): Flow<List<ShipmentEntity>>

    @Query("SELECT * FROM shipments WHERE id = :id")
    suspend fun getShipmentById(id: String): ShipmentEntity?

    @Query("SELECT * FROM shipments WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getShipmentsByCustomer(customerId: String): Flow<List<ShipmentEntity>>

    @Query("SELECT * FROM shipments WHERE driverId = :driverId ORDER BY createdAt DESC")
    fun getShipmentsByDriver(driverId: String): Flow<List<ShipmentEntity>>

    @Query("SELECT * FROM shipments WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedShipments(): List<ShipmentEntity>

    @Query("SELECT * FROM shipments WHERE isOffline = 1")
    suspend fun getOfflineShipments(): List<ShipmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: ShipmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipments(shipments: List<ShipmentEntity>)

    @Update
    suspend fun updateShipment(shipment: ShipmentEntity)

    @Delete
    suspend fun deleteShipment(shipment: ShipmentEntity)

    @Query("DELETE FROM shipments WHERE id = :id")
    suspend fun deleteShipmentById(id: String)

    @Query("UPDATE shipments SET syncStatus = :status WHERE id = :id")
    suspend fun updateShipmentSyncStatus(id: String, status: String)

    @Query("UPDATE shipments SET lastSyncTime = :timestamp WHERE id = :id")
    suspend fun updateShipmentSyncTime(id: String, timestamp: Long)
}

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers ORDER BY name ASC")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE id = :id")
    suspend fun getDriverById(id: String): DriverEntity?

    @Query("SELECT * FROM drivers WHERE isAvailable = 1")
    suspend fun getAvailableDrivers(): List<DriverEntity>

    @Query("SELECT * FROM drivers WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedDrivers(): List<DriverEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverEntity>)

    @Update
    suspend fun updateDriver(driver: DriverEntity)

    @Delete
    suspend fun deleteDriver(driver: DriverEntity)

    @Query("UPDATE drivers SET syncStatus = :status WHERE id = :id")
    suspend fun updateDriverSyncStatus(id: String, status: String)
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY licensePlate ASC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE driverId = :driverId")
    suspend fun getVehiclesByDriver(driverId: String): List<VehicleEntity>

    @Query("SELECT * FROM vehicles WHERE status = 'AVAILABLE'")
    suspend fun getAvailableVehicles(): List<VehicleEntity>

    @Query("SELECT * FROM vehicles WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedVehicles(): List<VehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("UPDATE vehicles SET syncStatus = :status WHERE id = :id")
    suspend fun updateVehicleSyncStatus(id: String, status: String)
}

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations WHERE shipmentId = :shipmentId ORDER BY timestamp DESC")
    fun getLocationsByShipment(shipmentId: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE driverId = :driverId ORDER BY timestamp DESC")
    fun getLocationsByDriver(driverId: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE syncStatus = 'PENDING'")
    suspend fun getPendingLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE timestamp >= :since")
    suspend fun getLocationsSince(since: Long): List<LocationEntity>

    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Insert
    suspend fun insertLocations(locations: List<LocationEntity>)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("DELETE FROM locations WHERE timestamp < :before")
    suspend fun deleteOldLocations(before: Long)

    @Query("UPDATE locations SET syncStatus = :status WHERE id = :id")
    suspend fun updateLocationSyncStatus(id: String, status: String)
}

@Dao
interface SyncOperationDao {
    @Query("SELECT * FROM sync_operations WHERE status = 'PENDING' ORDER BY timestamp ASC")
    suspend fun getPendingOperations(): List<SyncOperationEntity>

    @Query("SELECT * FROM sync_operations WHERE status = 'FAILED' AND nextRetryTime <= :currentTime ORDER BY nextRetryTime ASC")
    suspend fun getRetryableOperations(currentTime: Long): List<SyncOperationEntity>

    @Query("SELECT * FROM sync_operations WHERE entityType = :entityType AND entityId = :entityId ORDER BY timestamp DESC")
    suspend fun getOperationsByEntity(entityType: String, entityId: String): List<SyncOperationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: SyncOperationEntity)

    @Update
    suspend fun updateOperation(operation: SyncOperationEntity)

    @Delete
    suspend fun deleteOperation(operation: SyncOperationEntity)

    @Query("DELETE FROM sync_operations WHERE status = 'COMPLETED' AND timestamp < :before")
    suspend fun deleteCompletedOperations(before: Long)

    @Query("UPDATE sync_operations SET status = :status, retryCount = :retryCount, lastError = :error, nextRetryTime = :nextRetryTime WHERE id = :id")
    suspend fun updateOperationStatus(
        id: String,
        status: String,
        retryCount: Int,
        error: String?,
        nextRetryTime: Long
    )
}

@Dao
interface CachedMapsDataDao {
    @Query("SELECT * FROM cached_maps_data WHERE cacheKey = :key")
    suspend fun getCachedData(key: String): CachedMapsDataEntity?

    @Query("SELECT * FROM cached_maps_data WHERE dataType = :type AND expiryTime > :currentTime")
    suspend fun getValidCachedDataByType(type: String, currentTime: Long): List<CachedMapsDataEntity>

    @Query("SELECT * FROM cached_maps_data WHERE expiryTime <= :currentTime")
    suspend fun getExpiredData(currentTime: Long): List<CachedMapsDataEntity>

    @Query("SELECT * FROM cached_maps_data ORDER BY accessCount DESC, lastAccessed DESC LIMIT :limit")
    suspend fun getMostAccessedData(limit: Int): List<CachedMapsDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedData(data: CachedMapsDataEntity)

    @Update
    suspend fun updateCachedData(data: CachedMapsDataEntity)

    @Delete
    suspend fun deleteCachedData(data: CachedMapsDataEntity)

    @Query("DELETE FROM cached_maps_data WHERE expiryTime <= :currentTime")
    suspend fun deleteExpiredData(currentTime: Long)

    @Query("DELETE FROM cached_maps_data WHERE cacheKey = :key")
    suspend fun deleteCachedDataByKey(key: String)

    @Query("UPDATE cached_maps_data SET accessCount = accessCount + 1, lastAccessed = :timestamp WHERE cacheKey = :key")
    suspend fun updateAccessStats(key: String, timestamp: Long)

    @Query("SELECT SUM(size) FROM cached_maps_data")
    suspend fun getTotalCacheSize(): Long?
}

@Dao
interface OfflineSettingsDao {
    @Query("SELECT * FROM offline_settings")
    suspend fun getAllSettings(): List<OfflineSettingsEntity>

    @Query("SELECT * FROM offline_settings WHERE key = :key")
    suspend fun getSettingByKey(key: String): OfflineSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: OfflineSettingsEntity)

    @Update
    suspend fun updateSetting(setting: OfflineSettingsEntity)

    @Delete
    suspend fun deleteSetting(setting: OfflineSettingsEntity)

    @Query("DELETE FROM offline_settings WHERE key = :key")
    suspend fun deleteSettingByKey(key: String)
}

@Database(
    entities = [
        ShipmentEntity::class,
        DriverEntity::class,
        VehicleEntity::class,
        LocationEntity::class,
        SyncOperationEntity::class,
        CachedMapsDataEntity::class,
        OfflineSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun shipmentDao(): ShipmentDao
    abstract fun driverDao(): DriverDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun locationDao(): LocationDao
    abstract fun syncOperationDao(): SyncOperationDao
    abstract fun cachedMapsDataDao(): CachedMapsDataDao
    abstract fun offlineSettingsDao(): OfflineSettingsDao
}

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}
