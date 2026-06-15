package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.DriverEntity

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers")
    suspend fun getAllDrivers(): List<DriverEntity>

    @Query("SELECT * FROM drivers WHERE id = :driverId")
    suspend fun getDriverById(driverId: String): DriverEntity?

    @Query("SELECT * FROM drivers WHERE status = :status")
    suspend fun getDriversByStatus(status: String): List<DriverEntity>

    @Query("SELECT * FROM drivers ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getDriversPaged(limit: Int, offset: Int): List<DriverEntity>

    @Query("SELECT * FROM drivers WHERE status = 'available' ORDER BY name ASC")
    suspend fun getAvailableDrivers(): List<DriverEntity>

    @Query("SELECT * FROM drivers WHERE status = 'on_trip' ORDER BY name ASC")
    suspend fun getDriversOnTrip(): List<DriverEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDrivers(drivers: List<DriverEntity>)

    @Update
    suspend fun updateDriver(driver: DriverEntity)

    @Delete
    suspend fun deleteDriver(driver: DriverEntity)

    @Query("DELETE FROM drivers WHERE id = :driverId")
    suspend fun deleteDriverById(driverId: String)

    @Query("UPDATE drivers SET status = :status WHERE id = :driverId")
    suspend fun updateDriverStatus(driverId: String, status: String)

    @Query("UPDATE drivers SET currentLatitude = :latitude, currentLongitude = :longitude, currentLocation = :address WHERE id = :driverId")
    suspend fun updateDriverLocation(driverId: String, latitude: Double, longitude: Double, address: String)

    @Query("UPDATE drivers SET rating = :rating WHERE id = :driverId")
    suspend fun updateDriverRating(driverId: String, rating: Float)

    @Query("SELECT * FROM drivers WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchDrivers(query: String): List<DriverEntity>

    @Query("UPDATE drivers SET currentVehicleId = :vehicleId WHERE id = :driverId")
    suspend fun assignDriverToVehicle(driverId: String, vehicleId: String)

    @Query("UPDATE drivers SET currentVehicleId = NULL WHERE id = :driverId")
    suspend fun unassignDriverFromVehicle(driverId: String)
}
