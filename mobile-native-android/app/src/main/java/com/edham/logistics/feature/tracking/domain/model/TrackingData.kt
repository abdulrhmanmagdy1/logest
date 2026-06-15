package com.edham.logistics.feature.tracking.domain.model

import com.google.android.gms.maps.model.LatLng

data class LocationUpdate(
    val id: String,
    val shipmentId: String,
    val driverId: String,
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val heading: Float,
    val altitude: Double,
    val timestamp: Long,
    val batteryLevel: Float?,
    val isMoving: Boolean,
    val address: String?
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}

data class ShipmentTracking(
    val id: String,
    val shipmentId: String,
    val trackingNumber: String,
    val driverId: String,
    val driverName: String,
    val driverPhone: String,
    val vehicleId: String,
    val vehicleLicensePlate: String,
    val vehicleType: String,
    val currentLocation: LatLng,
    val pickupLocation: LatLng,
    val destinationLocation: LatLng,
    val status: TrackingStatus,
    val estimatedArrivalTime: Long?,
    val distanceRemaining: Double,
    val durationRemaining: Long,
    val routePoints: List<LatLng>,
    val lastUpdate: Long,
    val batteryLevel: Float?,
    val isMoving: Boolean,
    val speed: Float,
    val heading: Float,
    val temperature: Double?,
    val fuelLevel: Float?,
    val address: String?
)

data class RouteHistory(
    val id: String,
    val shipmentId: String,
    val trackingNumber: String,
    val driverId: String,
    val vehicleId: String,
    val routePoints: List<RoutePoint>,
    val startTime: Long,
    val endTime: Long?,
    val totalDistance: Double,
    val totalDuration: Long,
    val averageSpeed: Double,
    val maxSpeed: Double,
    val stops: List<RouteStop>,
    val events: List<RouteEvent>
)

data class RoutePoint(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val heading: Float,
    val timestamp: Long,
    val batteryLevel: Float?,
    val isMoving: Boolean,
    val address: String?
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}

data class RouteStop(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val reason: String?
)

data class RouteEvent(
    val id: String,
    val type: RouteEventType,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val metadata: Map<String, Any>?
)

data class FleetTracking(
    val vehicles: List<VehicleTracking>,
    val activeShipments: List<ShipmentTracking>,
    val lastUpdate: Long
)

data class VehicleTracking(
    val id: String,
    val driverId: String,
    val driverName: String,
    val driverPhone: String,
    val vehicleId: String,
    val vehicleLicensePlate: String,
    val vehicleType: String,
    val currentLocation: LatLng,
    val status: VehicleStatus,
    val isOnline: Boolean,
    val lastUpdate: Long,
    val batteryLevel: Float?,
    val fuelLevel: Float?,
    val temperature: Double?,
    val speed: Float,
    val heading: Float,
    val currentShipmentId: String?,
    val currentShipmentNumber: String?
)

data class TrackingSettings(
    val updateInterval: Long, // milliseconds
    val minDistanceForUpdate: Float, // meters
    val maxAccuracyThreshold: Float, // meters
    val batteryOptimization: Boolean,
    val nightModeUpdates: Boolean,
    val stopDetectionThreshold: Long, // milliseconds
    val speedThreshold: Float, // km/h
    val enableRouteOptimization: Boolean,
    val enableETAUpdates: Boolean
)

data class TrackingStatistics(
    val totalDistance: Double,
    val totalDuration: Long,
    val averageSpeed: Double,
    val maxSpeed: Double,
    val idleTime: Long,
    val movingTime: Long,
    val stopsCount: Int,
    val routeEfficiency: Double, // percentage
    val onTimeDeliveryRate: Double, // percentage
    val fuelConsumption: Double?, // liters
    val batteryUsage: Double // percentage
)

enum class TrackingStatus {
    NOT_STARTED,
    EN_ROUTE_TO_PICKUP,
    AT_PICKUP,
    EN_ROUTE_TO_DESTINATION,
    AT_DESTINATION,
    DELIVERED,
    DELAYED,
    CANCELLED,
    ERROR
}

enum class VehicleStatus {
    IDLE,
    MOVING,
    STOPPED,
    OFFLINE,
    MAINTENANCE,
    BREAKDOWN
}

enum class RouteEventType {
    STARTED,
    PICKUP_REACHED,
    PICKUP_COMPLETED,
    DESTINATION_REACHED,
    DELIVERY_COMPLETED,
    STOP_DETECTED,
    SPEED_EXCEEDED,
    OFFLINE_DETECTED,
    BATTERY_LOW,
    TEMPERATURE_ALERT,
    ROUTE_DEVIATION,
    DELAY_DETECTED
}

// Request/Response models
data class StartTrackingRequest(
    val shipmentId: String,
    val driverId: String,
    val vehicleId: String,
    val pickupLocation: LatLng,
    val destinationLocation: LatLng
)

data class UpdateLocationRequest(
    val shipmentId: String,
    val driverId: String,
    val vehicleId: String,
    val location: LatLng,
    val accuracy: Float,
    val speed: Float,
    val heading: Float,
    val batteryLevel: Float?,
    val isMoving: Boolean
)

data class GetTrackingRequest(
    val shipmentId: String?,
    val driverId: String?,
    val customerId: String?,
    val status: TrackingStatus?
)

data class GetFleetTrackingRequest(
    val vehicleIds: List<String>?,
    val driverIds: List<String>?,
    val activeOnly: Boolean
)

data class RouteOptimizationRequest(
    val shipmentId: String,
    val currentLocation: LatLng,
    val destinationLocation: LatLng,
    val waypoints: List<LatLng>?,
    val avoidTolls: Boolean,
    val avoidHighways: Boolean,
    val optimizeFor: OptimizationCriteria
)

enum class OptimizationCriteria {
    TIME,
    DISTANCE,
    FUEL,
    TRAFFIC
}

// WebSocket/Real-time models
data class TrackingWebSocketMessage(
    val type: TrackingMessageType,
    val data: Any,
    val timestamp: Long,
    val shipmentId: String?,
    val driverId: String?,
    val vehicleId: String?
)

enum class TrackingMessageType {
    LOCATION_UPDATE,
    STATUS_CHANGE,
    ETA_UPDATE,
    ROUTE_DEVIATION,
    BATTERY_LOW,
    TEMPERATURE_ALERT,
    DRIVER_OFFLINE,
    VEHICLE_STOPPED,
    DELIVERY_COMPLETED,
    ERROR
}
