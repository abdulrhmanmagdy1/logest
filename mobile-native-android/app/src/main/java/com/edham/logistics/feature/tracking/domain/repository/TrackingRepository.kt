package com.edham.logistics.feature.tracking.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.tracking.domain.model.*
import kotlinx.coroutines.flow.Flow
import com.google.android.gms.maps.model.LatLng

interface TrackingRepository {
    // Tracking Management
    suspend fun startTracking(request: StartTrackingRequest): Result<ShipmentTracking>
    suspend fun stopTracking(shipmentId: String): Result<Unit>
    suspend fun pauseTracking(shipmentId: String): Result<Unit>
    suspend fun resumeTracking(shipmentId: String): Result<Unit>
    
    // Location Updates
    suspend fun updateLocation(request: UpdateLocationRequest): Result<LocationUpdate>
    suspend fun batchUpdateLocations(requests: List<UpdateLocationRequest>): Result<List<LocationUpdate>>
    
    // Shipment Tracking
    suspend fun getShipmentTracking(shipmentId: String): Result<ShipmentTracking>
    suspend fun observeShipmentTracking(shipmentId: String): Flow<Result<ShipmentTracking>>
    suspend fun getCustomerTracking(customerId: String): Result<List<ShipmentTracking>>
    suspend fun getDriverTracking(driverId: String): Result<List<ShipmentTracking>>
    
    // Fleet Tracking
    suspend fun getFleetTracking(request: GetFleetTrackingRequest): Result<FleetTracking>
    suspend fun observeFleetTracking(): Flow<Result<FleetTracking>>
    suspend fun getVehicleTracking(vehicleId: String): Result<VehicleTracking>
    suspend fun observeVehicleTracking(vehicleId: String): Flow<Result<VehicleTracking>>
    
    // Route History
    suspend fun getRouteHistory(shipmentId: String): Result<RouteHistory>
    suspend fun observeRouteHistory(shipmentId: String): Flow<Result<RouteHistory>>
    suspend fun getDriverRouteHistory(driverId: String, limit: Int): Result<List<RouteHistory>>
    suspend fun getCustomerRouteHistory(customerId: String, limit: Int): Result<List<RouteHistory>>
    
    // ETA and Route Optimization
    suspend fun calculateETA(
        currentLocation: LatLng,
        destinationLocation: LatLng,
        currentSpeed: Float,
        trafficData: Map<String, Any>?
    ): Result<Long>
    
    suspend fun getOptimizedRoute(request: RouteOptimizationRequest): Result<List<LatLng>>
    suspend fun getRouteDistance(route: List<LatLng>): Result<Double>
    suspend fun getRouteDuration(route: List<LatLng>): Result<Long>
    
    // Tracking Settings
    suspend fun getTrackingSettings(driverId: String): Result<TrackingSettings>
    suspend fun updateTrackingSettings(driverId: String, settings: TrackingSettings): Result<TrackingSettings>
    suspend fun getDefaultTrackingSettings(): Result<TrackingSettings>
    
    // Statistics
    suspend fun getTrackingStatistics(shipmentId: String): Result<TrackingStatistics>
    suspend fun getDriverTrackingStatistics(driverId: String, period: String): Result<TrackingStatistics>
    suspend fun getFleetTrackingStatistics(period: String): Result<TrackingStatistics>
    
    // WebSocket/Real-time
    suspend fun connectToTrackingWebSocket(): Result<Unit>
    suspend fun disconnectFromTrackingWebSocket(): Result<Unit>
    suspend fun observeTrackingMessages(): Flow<Result<TrackingWebSocketMessage>>
    suspend fun sendTrackingMessage(message: TrackingWebSocketMessage): Result<Unit>
    
    // Location Services Integration
    suspend fun getCurrentLocation(): Result<LatLng>
    suspend fun getAddressFromLocation(location: LatLng): Result<String>
    suspend fun getLocationFromAddress(address: String): Result<LatLng>
    
    // Battery Optimization
    suspend fun enableBatteryOptimization(): Result<Unit>
    suspend fun disableBatteryOptimization(): Result<Unit>
    suspend fun isBatteryOptimizationEnabled(): Result<Boolean>
    
    // Route Deviation Detection
    suspend fun detectRouteDeviation(
        shipmentId: String,
        currentLocation: LatLng,
        plannedRoute: List<LatLng>
    ): Result<Boolean>
    
    suspend fun getRouteDeviationThreshold(): Result<Double>
    suspend fun updateRouteDeviationThreshold(threshold: Double): Result<Double>
    
    // Geofencing
    suspend fun createGeofence(
        id: String,
        location: LatLng,
        radius: Float,
        type: GeofenceType
    ): Result<Unit>
    
    suspend fun removeGeofence(id: String): Result<Unit>
    suspend fun observeGeofenceEvents(): Flow<Result<GeofenceEvent>>
}

enum class GeofenceType {
    PICKUP,
    DESTINATION,
    WAYPOINT,
    RESTRICTED_AREA,
    SERVICE_AREA
}

data class GeofenceEvent(
    val id: String,
    val type: GeofenceType,
    val action: GeofenceAction,
    val location: LatLng,
    val timestamp: Long,
    val shipmentId: String?,
    val driverId: String?
)

enum class GeofenceAction {
    ENTER,
    EXIT,
    DWELL
}
