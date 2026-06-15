package com.edham.logistics.feature.driver.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.DriverProfile
import com.edham.logistics.feature.driver.domain.model.ShipmentSummary
import com.edham.logistics.feature.driver.domain.model.DriverLocation
import com.edham.logistics.feature.driver.domain.model.DriverDashboardData
import com.edham.logistics.feature.driver.domain.model.TripUpdateRequest
import com.edham.logistics.feature.driver.domain.model.TripActionRequest
import com.edham.logistics.feature.driver.domain.model.DeliveryProofRequest
import kotlinx.coroutines.flow.Flow

interface DriverRepository {
    suspend fun getDriverProfile(driverId: String): Result<DriverProfile>
    suspend fun updateDriverProfile(profile: DriverProfile): Result<DriverProfile>
    suspend fun getDriverShipments(driverId: String, page: Int = 1, pageSize: Int = 20): Result<List<ShipmentSummary>>
    suspend fun updateShipmentStatus(shipmentId: String, status: String): Result<Unit>
    suspend fun updateDriverLocation(location: DriverLocation): Result<Unit>
    suspend fun startTrip(shipmentId: String): Result<Unit>
    suspend fun completeTrip(shipmentId: String, deliveryPhotos: List<String>): Result<Unit>
    suspend fun cancelTrip(shipmentId: String, reason: String): Result<Unit>
    suspend fun reportIssue(shipmentId: String, issue: String, photos: List<String>): Result<Unit>
    
    // New driver-specific methods
    suspend fun getDriverDashboard(driverId: String): Flow<Result<DriverDashboardData>>
    suspend fun performTripAction(request: TripActionRequest): Result<Unit>
    suspend fun updateTripStatus(request: TripUpdateRequest): Result<Unit>
    suspend fun uploadDeliveryProof(tripId: String, request: DeliveryProofRequest): Result<Unit>
    
    fun observeDriverProfile(driverId: String): Flow<DriverProfile>
    fun observeDriverShipments(driverId: String): Flow<List<ShipmentSummary>>
    fun observeDriverLocation(driverId: String): Flow<DriverLocation?>
}
