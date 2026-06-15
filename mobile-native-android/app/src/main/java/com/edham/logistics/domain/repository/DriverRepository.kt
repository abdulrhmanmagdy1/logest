package com.edham.logistics.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Driver
import kotlinx.coroutines.flow.Flow

interface DriverRepository {
    suspend fun getDrivers(page: Int, pageSize: Int): Result<List<Driver>>
    suspend fun getDriverById(id: String): Result<Driver>
    suspend fun createDriver(driver: Driver): Result<Driver>
    suspend fun updateDriver(driver: Driver): Result<Driver>
    suspend fun deleteDriver(id: String): Result<Unit>
    suspend fun getDriversByStatus(status: String): Result<List<Driver>>
    suspend fun getAvailableDrivers(): Result<List<Driver>>
    suspend fun getDriversOnTrip(): Result<List<Driver>>
    suspend fun updateDriverStatus(id: String, status: String): Result<Unit>
    suspend fun updateDriverLocation(id: String, latitude: Double, longitude: Double, address: String): Result<Unit>
    suspend fun updateDriverRating(id: String, rating: Float): Result<Unit>
    suspend fun searchDrivers(query: String): Result<List<Driver>>
    fun observeDrivers(): Flow<List<Driver>>
    fun observeDriverById(id: String): Flow<Driver?>
    suspend fun assignDriverToVehicle(driverId: String, vehicleId: String): Result<Unit>
    suspend fun unassignDriverFromVehicle(driverId: String): Result<Unit>
    // suspend fun getDriverPerformance(id: String): Result<com.edham.logistics.domain.model.DriverPerformance>
}
