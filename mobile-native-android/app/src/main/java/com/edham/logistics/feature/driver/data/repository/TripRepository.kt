package com.edham.logistics.feature.driver.data.repository

import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.data.local.dao.DriverFeatureDao
import com.edham.logistics.data.local.entity.TripEntity
import com.edham.logistics.data.local.entity.WaypointEntity
import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.data.models.Waypoint
import com.edham.logistics.core.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor(
    private val api: DriverApi,
    private val dao: DriverFeatureDao
) {

    fun getTrips(driverId: String, date: String): Flow<Resource<List<Trip>>> = flow {
        emit(Resource.Loading())
        
        // First emit from local cache
        val localTrips = dao.getTripsByDate(date).first().map { it.toDomain() }
        if (localTrips.isNotEmpty()) {
            emit(Resource.Success(localTrips))
        }

        try {
            val response = api.getTrips(driverId, date)
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteTrips = response.body()?.data ?: emptyList()
                dao.insertTrips(remoteTrips.map { it.toEntity(date) })
                emit(Resource.Success(remoteTrips))
            } else {
                emit(Resource.Error(response.message() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getActiveTrips(): Flow<Resource<List<Trip>>> = flow {
        emit(Resource.Loading())
        val local = dao.getActiveTrips().first().map { it.toDomain() }
        if (local.isNotEmpty()) {
            emit(Resource.Success(local))
        }
        // ... potentially fetch from API too
    }

    suspend fun addWaypoint(tripId: String, waypoint: Waypoint): Resource<Unit> {
        return try {
            val response = api.addWaypoint(tripId, waypoint)
            if (response.isSuccessful) {
                dao.insertWaypoint(waypoint.toEntity())
                Resource.Success(Unit)
            } else {
                // If API fails, we still save to local Room with PENDING status for offline sync
                dao.insertWaypoint(waypoint.toEntity().copy(syncStatus = "PENDING"))
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            dao.insertWaypoint(waypoint.toEntity().copy(syncStatus = "PENDING"))
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getTripPath(tripId: String): Flow<Resource<List<Waypoint>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getTripPath(tripId)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(response.body()?.data ?: emptyList()))
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    suspend fun acceptTrip(tripId: String): Resource<Unit> {
        return try {
            val response = api.acceptTrip(tripId)
            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error(response.message())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun rejectTrip(tripId: String, reason: String): Resource<Unit> {
        return try {
            val response = api.rejectTrip(tripId, reason)
            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error(response.message())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    private fun TripEntity.toDomain() = Trip(
        id = id, tripId = tripId, status = status, startTime = startTime, endTime = endTime,
        distance = distance, routeSummary = routeSummary, earnings = earnings,
        origin = origin, destination = destination, destLat = destLat, destLng = destLng
    )

    private fun Trip.toEntity(date: String) = TripEntity(
        id = id, tripId = tripId, status = status, startTime = startTime, endTime = endTime,
        distance = distance, routeSummary = routeSummary, earnings = earnings,
        origin = origin, destination = destination, destLat = destLat, destLng = destLng,
        date = date
    )

    private fun Waypoint.toEntity() = WaypointEntity(
        tripId = tripId, latitude = lat, longitude = lng, address = address,
        timestamp = timestamp
    )
}
