package com.edham.logistics.feature.customer.domain.model

import com.edham.logistics.domain.model.ShipmentStatus

data class CustomerDashboardData(
    val totalShipments: Int,
    val activeShipments: Int,
    val completedShipments: Int,
    val pendingShipments: Int,
    val recentShipments: List<CustomerShipment>,
    val activeShipmentsList: List<CustomerShipment>,
    val completedShipmentsList: List<CustomerShipment>,
    val pendingShipmentsList: List<CustomerShipment>
)

data class ShipmentCreationRequest(
    val customerId: String,
    val pickupAddress: String,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val deliveryAddress: String,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,
    val cargoType: String,
    val weight: Double,
    val notes: String? = null,
    val temperature: Double? = null,
    val priority: String = "normal",
    val insuranceValue: Double? = null
)

data class ShipmentTrackingData(
    val shipment: CustomerShipment,
    val currentLocation: TrackingLocation? = null,
    val trackingHistory: List<TrackingEvent>,
    val estimatedArrivalTime: String,
    val isRefrigerated: Boolean = false,
    val currentTemperature: Double? = null
)

data class TrackingLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val timestamp: String,
    val speed: Float? = null,
    val heading: Float? = null
)

data class CargoType(
    val id: String,
    val name: String,
    val displayName: String,
    val icon: String,
    val requiresTemperature: Boolean = false,
    val maxWeight: Double,
    val pricePerKg: Double,
    val pricePerKm: Double
)

data class ShipmentPriority(
    val id: String,
    val name: String,
    val displayName: String,
    val priceMultiplier: Double,
    val estimatedTime: String,
    val icon: String
)

// ShipmentStatus is now imported from domain.model

enum class CargoTypeEnum(val value: String, val displayName: String) {
    GENERAL("general", "بضائع عامة"),
    REFRIGERATED("refrigerated", "بضائع مبردة"),
    FRAGILE("fragile", "بضائع قابلة للكسر"),
    HEAVY("heavy", "بضائع ثقيلة"),
    URGENT("urgent", "بضائع عاجلة"),
    DOCUMENTS("documents", "مستندات"),
    ELECTRONICS("electronics", "إلكترونيات"),
    FOOD("food", "مواد غذائية"),
    MEDICAL("medical", "معدات طبية"),
    CHEMICALS("chemicals", "مواد كيميائية"),
    LIVESTOCK("livestock", "مواشي"),
    VEHICLES("vehicles", "مركبات")
}
