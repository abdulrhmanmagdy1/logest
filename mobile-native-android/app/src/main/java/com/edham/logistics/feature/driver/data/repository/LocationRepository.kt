package com.edham.logistics.feature.driver.data.repository

import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.data.local.dao.DriverFeatureDao
import com.edham.logistics.data.local.entity.LocationCacheEntity
import com.edham.logistics.feature.driver.data.models.LocationUpdate
import com.edham.logistics.core.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val api: DriverApi,
    private val dao: DriverFeatureDao
) {

    suspend fun updateLocation(driverId: String, location: LocationUpdate): Resource<Unit> {
        return try {
            val response = api.updateLocation(driverId, location)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                // Cache for offline sync
                cacheLocation(location)
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            cacheLocation(location)
            Resource.Error(e.message ?: "Network error")
        }
    }

    private suspend fun cacheLocation(location: LocationUpdate) {
        dao.insertLocation(LocationCacheEntity(
            latitude = location.lat,
            longitude = location.lng,
            accuracy = location.accuracy,
            speed = location.speed,
            heading = location.heading,
            timestamp = location.timestamp
        ))
    }

    suspend fun syncPendingLocations(driverId: String) {
        val pending = dao.getPendingLocations()
        if (pending.isEmpty()) return

        pending.forEach { 
            val locationUpdate = LocationUpdate(
                lat = it.latitude,
                lng = it.longitude,
                accuracy = it.accuracy,
                speed = it.speed,
                heading = it.heading,
                timestamp = it.timestamp
            )
            try {
                val response = api.updateLocation(driverId, locationUpdate)
                if (response.isSuccessful) {
                    dao.markLocationsSynced(listOf(it.id))
                }
            } catch (e: Exception) {
                // Ignore and retry later
            }
        }
    }
}
