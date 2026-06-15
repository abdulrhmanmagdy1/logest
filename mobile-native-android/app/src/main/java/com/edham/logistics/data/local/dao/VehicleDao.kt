package com.edham.logistics.data.local.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    
    @Query("SELECT * FROM vehicles WHERE id IN (:ids)")
    suspend fun getVehiclesByIds(ids: List<String>): List<VehicleEntity>

    @Query("SELECT * FROM vehicles")
    suspend fun getAllVehiclesList(): List<VehicleEntity>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE status = :status")
    fun getVehiclesByStatus(status: String): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getVehiclesPaged(limit: Int, offset: Int): List<VehicleEntity>

    @Query("SELECT * FROM vehicles WHERE status = 'available' ORDER BY name ASC")
    fun getAvailableVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE status = 'on_trip' ORDER BY name ASC")
    fun getVehiclesOnTrip(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE nextMaintenanceDate <= :currentDate ORDER BY nextMaintenanceDate ASC")
    suspend fun getVehiclesNeedingMaintenance(currentDate: String): List<VehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVehicles(vehicles: List<VehicleEntity>)

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    suspend fun deleteVehicleById(vehicleId: String)

    @Query("UPDATE vehicles SET status = :status WHERE id = :vehicleId")
    suspend fun updateVehicleStatus(vehicleId: String, status: String)

    @Query("UPDATE vehicles SET currentLatitude = :latitude, currentLongitude = :longitude, currentLocation = :address WHERE id = :vehicleId")
    suspend fun updateVehicleLocation(vehicleId: String, latitude: Double, longitude: Double, address: String)

    @Query("UPDATE vehicles SET currentFuelLevel = :fuelLevel WHERE id = :vehicleId")
    suspend fun updateFuelLevel(vehicleId: String, fuelLevel: Double)

    @Query("SELECT * FROM vehicles WHERE name LIKE '%' || :query || '%' OR plateNumber LIKE '%' || :query || '%' OR make LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchVehicles(query: String): List<VehicleEntity>

    @Query("UPDATE vehicles SET driverId = :driverId WHERE id = :vehicleId")
    suspend fun assignDriverToVehicle(vehicleId: String, driverId: String)

    @Query("UPDATE vehicles SET driverId = NULL WHERE id = :vehicleId")
    suspend fun unassignDriverFromVehicle(vehicleId: String)

    @Query("DELETE FROM vehicles")
    suspend fun deleteAllVehicles()

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun getVehiclesCount(): Int
    @Query("SELECT COUNT(*) FROM vehicles WHERE status = :status")
    suspend fun getVehiclesCountByStatus(status: String): Int
}
