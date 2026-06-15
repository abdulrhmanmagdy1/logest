package com.edham.logistics.feature.admin.domain.model

import com.edham.logistics.domain.model.ShipmentStatus

data class AdminDashboardData(
    val metrics: DashboardMetrics,
    val recentShipments: List<AdminShipment>,
    val activeTrips: List<AdminTrip>,
    val temperatureAlerts: List<TemperatureAlert>,
    val pendingOrders: List<AdminOrder>,
    val fleetStatus: FleetStatus,
    val userStats: UserStats
)

data class DashboardMetrics(
    val totalShipments: Int,
    val activeTrips: Int,
    val delayedShipments: Int,
    val activeDrivers: Int,
    val todayRevenue: Double,
    val monthlyRevenue: Double,
    val customerSatisfaction: Double,
    val onTimeDeliveryRate: Double
)

data class AdminShipment(
    val id: String,
    val trackingNumber: String,
    val customerName: String,
    val customerPhone: String,
    val pickupLocation: LocationPoint,
    val deliveryLocation: LocationPoint,
    val cargoType: String,
    val weight: Double,
    val priority: String,
    val price: Double,
    val status: ShipmentStatus,
    val assignedDriver: DriverInfo?,
    val createdAt: String,
    val estimatedDelivery: String,
    val actualDelivery: String?,
    val specialInstructions: String?,
    val temperature: Double?,
    val requiresTemperatureControl: Boolean
)

data class AdminTrip(
    val id: String,
    val shipmentId: String,
    val trackingNumber: String,
    val driver: DriverInfo,
    val status: TripStatus,
    val currentLocation: LocationPoint?,
    val route: RouteInfo?,
    val startedAt: String?,
    val estimatedArrival: String,
    val actualArrival: String?,
    val distance: Double,
    val duration: Int,
    val progress: Double // 0.0 to 1.0
)

data class TemperatureAlert(
    val id: String,
    val shipmentId: String,
    val trackingNumber: String,
    val driverName: String,
    val currentTemperature: Double,
    val requiredTemperature: Double,
    val alertLevel: AlertLevel,
    val location: LocationPoint,
    val timestamp: String,
    val isResolved: Boolean
)

data class AdminOrder(
    val id: String,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val pickupLocation: LocationPoint,
    val deliveryLocation: LocationPoint,
    val cargoType: String,
    val weight: Double,
    val priority: String,
    val estimatedPrice: Double,
    val specialInstructions: String?,
    val createdAt: String,
    val status: OrderStatus,
    val requiresApproval: Boolean
)

data class FleetStatus(
    val totalDrivers: Int,
    val activeDrivers: Int,
    val availableDrivers: Int,
    val offlineDrivers: Int,
    val vehicles: List<VehicleStatus>
)

data class VehicleStatus(
    val id: String,
    val licensePlate: String,
    val driverName: String,
    val status: VehicleStatusType,
    val currentLocation: LocationPoint?,
    val lastUpdate: String,
    val fuelLevel: Double?,
    val temperature: Double?
)

data class UserStats(
    val totalCustomers: Int,
    val activeCustomers: Int,
    val totalDrivers: Int,
    val activeDrivers: Int,
    val totalAccountants: Int,
    val activeAccountants: Int,
    val newUsersThisMonth: Int
)

data class DriverInfo(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val licenseNumber: String,
    val rating: Double,
    val totalTrips: Int,
    val isActive: Boolean,
    val currentLocation: LocationPoint?,
    val vehicle: VehicleInfo
)

data class VehicleInfo(
    val id: String,
    val type: String,
    val licensePlate: String,
    val capacity: Double,
    val temperatureControlled: Boolean,
    val fuelType: String
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

// ShipmentStatus is now imported from domain.model

enum class TripStatus {
    ASSIGNED,
    STARTED,
    IN_TRANSIT,
    ARRIVED,
    COMPLETED,
    CANCELLED
}

enum class OrderStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}

enum class AlertLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class VehicleStatusType {
    ACTIVE,
    IDLE,
    OFFLINE,
    MAINTENANCE
}

// Request/Response models
data class AssignDriverRequest(
    val shipmentId: String,
    val driverId: String,
    val notes: String?
)

data class ApproveOrderRequest(
    val orderId: String,
    val approvedBy: String,
    val notes: String?
)

data class RejectOrderRequest(
    val orderId: String,
    val rejectedBy: String,
    val reason: String,
    val notes: String?
)

data class CreateShipmentRequest(
    val customerId: String,
    val pickupLocation: LocationPoint,
    val deliveryLocation: LocationPoint,
    val cargoType: String,
    val weight: Double,
    val priority: String,
    val specialInstructions: String?,
    val temperature: Double?,
    val price: Double
)

data class UpdateUserRequest(
    val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val isActive: Boolean,
    val role: String
)

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean,
    val createdAt: String,
    val lastLogin: String?,
    val profile: UserProfile?
)

data class UserProfile(
    val driverInfo: DriverInfo?,
    val customerInfo: CustomerInfo?,
    val accountantInfo: AccountantInfo?
)

data class CustomerInfo(
    val totalShipments: Int,
    val totalSpent: Double,
    val averageRating: Double
)

data class AccountantInfo(
    val department: String,
    val employeeId: String,
    val managedAccounts: Int
)

enum class UserRole {
    ADMIN,
    DRIVER,
    CUSTOMER,
    ACCOUNTANT
}
