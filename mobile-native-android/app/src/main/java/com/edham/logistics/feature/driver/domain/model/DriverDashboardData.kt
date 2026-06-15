package com.edham.logistics.feature.driver.domain.model

data class DriverDashboardData(
    val driverInfo: DriverInfo,
    val todayStats: TodayStats,
    val assignedTrips: List<DriverTrip>,
    val activeTrip: DriverTrip?,
    val recentDeliveries: List<DriverTrip>
)

data class DriverInfo(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val licenseNumber: String,
    val vehicleInfo: VehicleInfo,
    val isOnline: Boolean,
    val currentLocation: LocationPoint?
)

data class VehicleInfo(
    val id: String,
    val type: String,
    val licensePlate: String,
    val capacity: Double,
    val temperatureControlled: Boolean
)

data class TodayStats(
    val completedTrips: Int,
    val totalDistance: Double,
    val totalEarnings: Double,
    val averageRating: Double
)

data class DriverTrip(
    val id: String,
    val trackingNumber: String,
    val customerName: String,
    val customerPhone: String,
    val pickupLocation: LocationPoint,
    val deliveryLocation: LocationPoint,
    val cargoType: String,
    val weight: Double,
    val priority: String,
    val specialInstructions: String?,
    val estimatedDuration: Int,
    val estimatedDistance: Double,
    val price: Double,
    val status: TripStatus,
    val assignedAt: String,
    val acceptedAt: String?,
    val startedAt: String?,
    val completedAt: String?,
    val deliveryProof: DeliveryProof?,
    val currentLocation: LocationPoint?,
    val route: RouteInfo?
)

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val timestamp: String?
)

data class RouteInfo(
    val distance: Double,
    val duration: Int,
    val steps: List<RouteStep>
)

data class RouteStep(
    val instruction: String,
    val distance: Double,
    val duration: Int,
    val startLocation: LocationPoint,
    val endLocation: LocationPoint
)

data class DeliveryProof(
    val id: String,
    val photoUrl: String?,
    val signatureUrl: String?,
    val recipientName: String,
    val recipientPhone: String,
    val notes: String?,
    val timestamp: String,
    val location: LocationPoint
)

enum class TripStatus {
    ASSIGNED,
    ACCEPTED,
    REJECTED,
    STARTED,
    IN_TRANSIT,
    ARRIVED,
    DELIVERED,
    CANCELLED
}

data class TripUpdateRequest(
    val tripId: String,
    val status: TripStatus,
    val location: LocationPoint?,
    val notes: String?,
    val deliveryProof: DeliveryProofRequest?
)

data class DeliveryProofRequest(
    val photoBase64: String?,
    val signatureBase64: String?,
    val recipientName: String,
    val recipientPhone: String,
    val notes: String?,
    val location: LocationPoint
)

data class TripActionRequest(
    val tripId: String,
    val action: TripAction,
    val notes: String?
)

enum class TripAction {
    ACCEPT,
    REJECT,
    START,
    CANCEL,
    COMPLETE
}
