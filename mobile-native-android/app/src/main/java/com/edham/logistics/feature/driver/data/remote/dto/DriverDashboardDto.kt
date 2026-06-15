package com.edham.logistics.feature.driver.data.remote.dto

data class DriverDashboardDto(
    val driverInfo: DriverInfoDto,
    val todayStats: TodayStatsDto,
    val assignedTrips: List<DriverTripDto>,
    val activeTrip: DriverTripDto?,
    val recentDeliveries: List<DriverTripDto>
)

data class DriverInfoDto(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val licenseNumber: String,
    val vehicleInfo: VehicleInfoDto,
    val isOnline: Boolean,
    val currentLocation: LocationDto?
)

data class VehicleInfoDto(
    val id: String,
    val type: String,
    val licensePlate: String,
    val capacity: Double,
    val temperatureControlled: Boolean
)

data class TodayStatsDto(
    val completedTrips: Int,
    val totalDistance: Double,
    val totalEarnings: Double,
    val averageRating: Double
)

data class DriverTripDto(
    val id: String,
    val trackingNumber: String,
    val customerName: String,
    val customerPhone: String,
    val pickupLocation: LocationDto,
    val deliveryLocation: LocationDto,
    val cargoType: String,
    val weight: Double,
    val priority: String,
    val specialInstructions: String?,
    val estimatedDuration: Int,
    val estimatedDistance: Double,
    val price: Double,
    val status: String,
    val assignedAt: String,
    val acceptedAt: String?,
    val startedAt: String?,
    val completedAt: String?,
    val deliveryProof: DeliveryProofDto?,
    val currentLocation: LocationDto?,
    val route: RouteDto?
)

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val timestamp: String?
)

data class RouteDto(
    val distance: Double,
    val duration: Int,
    val steps: List<RouteStepDto>
)

data class RouteStepDto(
    val instruction: String,
    val distance: Double,
    val duration: Int,
    val startLocation: LocationDto,
    val endLocation: LocationDto
)

data class DeliveryProofDto(
    val id: String,
    val photoUrl: String?,
    val signatureUrl: String?,
    val recipientName: String,
    val recipientPhone: String,
    val notes: String?,
    val timestamp: String,
    val location: LocationDto
)

data class TripActionDto(
    val tripId: String,
    val action: String,
    val notes: String?
)

data class TripUpdateDto(
    val tripId: String,
    val status: String,
    val location: LocationDto?,
    val notes: String?,
    val deliveryProof: DeliveryProofDto?
)

data class IssueReportDto(
    val issue: String,
    val photos: List<String>
)

data class DriverProfileDto(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val licenseNumber: String,
    val vehicleInfo: VehicleInfoDto,
    val isOnline: Boolean
)

data class ShipmentSummaryDto(
    val id: String,
    val trackingNumber: String,
    val customerName: String,
    val pickupLocation: String,
    val deliveryLocation: String,
    val status: String,
    val assignedAt: String,
    val price: Double
)

data class DriverLocationDto(
    val driverId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)
