package com.edham.logistics.data.repository

import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.feature.driver.data.models.LocationUpdate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverRepository @Inject constructor(
    private val api: DriverApi
) {
    suspend fun updateLocation(id: String, lat: Double, lng: Double) = 
        api.updateLocation(id, LocationUpdate(lat, lng, 0f, 0f, 0f, System.currentTimeMillis()))
        
    suspend fun getDriverStats(id: String) = api.getDriverStats(id)
    
    suspend fun getTrips(id: String, status: String? = null) = api.getTrips(id, status = status)
    
    suspend fun acceptTrip(id: String) = api.acceptTrip(id)
    
    suspend fun rejectTrip(id: String, reason: String) = api.rejectTrip(id, reason)
    
    suspend fun updateTripStatus(id: String, status: String, notes: String? = null) = 
        api.updateTripStatus(id, status, notes)
        
    suspend fun submitExpense(driverId: String, tripId: String?, amount: Double, type: String, desc: String, url: String?) =
        api.submitExpense(driverId, tripId, amount, type, desc, url)
        
    suspend fun reportTelemetry(id: String, battery: Int, signal: Int, temp: Double? = null) =
        api.reportTelemetry(id, battery, signal, temp)
}
