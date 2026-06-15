package com.edham.logistics.data.repository

import com.edham.logistics.core.network.ApiService
import com.edham.logistics.core.utils.Result
import com.edham.logistics.data.local.database.dao.LocationDao
import com.edham.logistics.data.local.entity.LocationEntity
import com.edham.logistics.data.remote.dto.request.LocationUpdateRequest
import com.edham.logistics.data.remote.mapper.LocationMapper
import com.edham.logistics.domain.model.Location
import com.edham.logistics.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao,
    private val apiService: ApiService
) : LocationRepository {

    override suspend fun getCurrentLocation(driverId: String): Result<Location> {
        return try {
            val locations = locationDao.getLocationsByVehicle(driverId)
            if (locations.isNotEmpty()) {
                val latestLocation = locations.first()
                Result.Success(LocationMapper.toDomain(latestLocation, driverId))
            } else {
                Result.Error(Exception("No location found for driver"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateLocation(location: Location): Result<Unit> {
        return try {
            // Save to local database
            val entity = LocationMapper.toEntity(location)
            locationDao.insertLocation(entity)

            // Send to backend
            val request = LocationUpdateRequest(
                vehicleId = location.vehicleId,
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude,
                speed = location.speed.toDouble(),
                heading = location.heading.toDouble(),
                timestamp = location.timestamp
            )
            // TODO: Uncomment when API endpoint is available
            // apiService.updateLocation(request)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getLocationHistory(
        driverId: String,
        startDate: Long,
        endDate: Long
    ): Result<List<Location>> {
        return try {
            val locations = locationDao.getLocationsByVehicle(driverId)
            val filteredLocations = locations.filter {
                it.timestamp.time >= startDate && it.timestamp.time <= endDate
            }
            Result.Success(filteredLocations.map { LocationMapper.toDomain(it, driverId) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getLatestLocationByVehicle(vehicleId: String): Result<Location?> {
        return try {
            val locations = locationDao.getLocationsByVehicle(vehicleId)
            if (locations.isNotEmpty()) {
                val latestLocation = locations.first()
                Result.Success(LocationMapper.toDomain(latestLocation, vehicleId))
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getLocationsByVehicle(vehicleId: String): Result<List<Location>> {
        return try {
            val locations = locationDao.getLocationsByVehicle(vehicleId)
            Result.Success(locations.map { LocationMapper.toDomain(it, vehicleId) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun batchUpdateLocations(locations: List<Location>): Result<Unit> {
        return try {
            val entities = locations.map { LocationMapper.toEntity(it) }
            locationDao.insertAllLocations(entities)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteOldLocations(beforeDate: Long): Result<Unit> {
        return try {
            locationDao.deleteLocationsBefore(beforeDate)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeLocationUpdates(driverId: String): Flow<Location> {
        return locationDao.observeLocationsByVehicle(driverId)
            .map { if (it.isNotEmpty()) LocationMapper.toDomain(it.first(), driverId) else Location(driverId = driverId, vehicleId = "", latitude = 0.0, longitude = 0.0, timestamp = System.currentTimeMillis()) }
    }

    override fun observeVehicleLocation(vehicleId: String): Flow<Location?> {
        return locationDao.observeLatestLocationByVehicle(vehicleId)
            .map { it?.let { LocationMapper.toDomain(it, vehicleId) } }
    }
}
