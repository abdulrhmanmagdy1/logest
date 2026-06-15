package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE vehicle_id = :vehicleId ORDER BY timestamp DESC")
    suspend fun getLocationsByVehicle(vehicleId: String): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE vehicle_id = :vehicleId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLocationByVehicle(vehicleId: String): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLocations(locations: List<LocationEntity>)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("DELETE FROM locations WHERE vehicle_id = :vehicleId")
    suspend fun deleteLocationsByVehicle(vehicleId: String)

    @Query("DELETE FROM locations WHERE timestamp < :beforeDate")
    suspend fun deleteLocationsBefore(beforeDate: Long)

    @Query("SELECT * FROM locations WHERE vehicle_id = :vehicleId ORDER BY timestamp DESC LIMIT 1")
    fun observeLatestLocationByVehicle(vehicleId: String): Flow<LocationEntity?>

    @Query("SELECT * FROM locations WHERE vehicle_id = :vehicleId ORDER BY timestamp DESC")
    fun observeLocationsByVehicle(vehicleId: String): Flow<List<LocationEntity>>
}
