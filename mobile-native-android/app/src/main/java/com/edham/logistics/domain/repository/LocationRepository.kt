package com.edham.logistics.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(driverId: String): Result<Location>
    suspend fun updateLocation(location: Location): Result<Unit>
    suspend fun getLocationHistory(driverId: String, startDate: Long, endDate: Long): Result<List<Location>>
    suspend fun getLatestLocationByVehicle(vehicleId: String): Result<Location?>
    suspend fun getLocationsByVehicle(vehicleId: String): Result<List<Location>>
    suspend fun batchUpdateLocations(locations: List<Location>): Result<Unit>
    suspend fun deleteOldLocations(beforeDate: Long): Result<Unit>
    fun observeLocationUpdates(driverId: String): Flow<Location>
    fun observeVehicleLocation(vehicleId: String): Flow<Location?>
}
