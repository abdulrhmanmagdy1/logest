package com.edham.logistics.domain.model

data class Vehicle(
    val id: String,
    val name: String,
    val plateNumber: String,
    val make: String,
    val model: String,
    val year: Int,
    val type: String,
    val capacity: Double, // in kg
    val dimensions: VehicleDimensions,
    val temperatureRange: TemperatureRange? = null,
    val fuelType: String,
    val fuelCapacity: Double, // in liters
    val currentFuelLevel: Double, // percentage
    val mileage: Double, // in km
    val status: String,
    val driverId: String? = null,
    val driverName: String? = null,
    val currentLocation: String? = null,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null,
    val lastMaintenanceDate: String,
    val nextMaintenanceDate: String,
    val insuranceExpiry: String,
    val registrationExpiry: String,
    val purchaseDate: String,
    val purchasePrice: Double,
    val currentValue: Double,
    val basePrice: Double,
    val pricePerKm: Double,
    val pricePerKg: Double,
    val features: List<String>,
    val documents: List<VehicleDocument>,
    val maintenanceRecords: List<MaintenanceRecord>,
    val performanceMetrics: VehiclePerformance,
    val photos: List<String> = emptyList()
)

data class VehicleDimensions(
    val length: Double, // in meters
    val width: Double, // in meters
    val height: Double, // in meters
    val volume: Double // in cubic meters
)

data class TemperatureRange(
    val min: Double, // in Celsius
    val max: Double // in Celsius
)

data class VehicleDocument(
    val id: String,
    val vehicleId: String,
    val documentType: String,
    val documentNumber: String,
    val issueDate: String,
    val expiryDate: String,
    val documentUrl: String,
    val status: String
)

data class MaintenanceRecord(
    val id: String,
    val vehicleId: String,
    val type: String,
    val description: String,
    val cost: Double,
    val performedBy: String,
    val performedAt: String,
    val nextDueDate: String,
    val odometer: Double,
    val notes: String? = null,
    val partsUsed: List<String> = emptyList()
)

data class VehiclePerformance(
    val averageFuelConsumption: Double, // liters/100km
    val totalDistance: Double, // in km
    val totalTrips: Int,
    val uptime: Float, // percentage
    val downtime: Float, // percentage
    val maintenanceCostPerKm: Double,
    val fuelEfficiencyScore: Float,
    val reliabilityScore: Float,
    val lastInspectionDate: String
)

enum class VehicleType(val value: String, val displayName: String) {
    TRUCK("truck", "شاحنة"),
    VAN("van", "فان"),
    REFRIGERATED_TRUCK("refrigerated_truck", "شاحنة مبردة"),
    FLATBED("flatbed", "مقطرة"),
    TANKER("tanker", "صهريج"),
    PICKUP("pickup", "بيك أب"),
    TRAILER("trailer", "مقطورة")
}

enum class VehicleStatus(val value: String, val displayName: String) {
    AVAILABLE("available", "متاحة"),
    ON_TRIP("on_trip", "في رحلة"),
    MAINTENANCE("maintenance", "في الصيانة"),
    OUT_OF_SERVICE("out_of_service", "خارج الخدمة"),
    ACCIDENT("accident", "في حادث"),
    INSPECTION("inspection", "في فحص"),
    RESERVED("reserved", "محجوزة")
}

enum class FuelType(val value: String, val displayName: String) {
    DIESEL("diesel", "ديزل"),
    PETROL("petrol", "بنزين"),
    ELECTRIC("electric", "كهربائي"),
    HYBRID("hybrid", "هايبرد"),
    GAS("gas", "غاز")
}

enum class MaintenanceType(val value: String, val displayName: String) {
    OIL_CHANGE("oil_change", "تغيير زيت"),
    TIRE_ROTATION("tire_rotation", "تدوير إطارات"),
    BRAKE_SERVICE("brake_service", "صيانة فرامل"),
    ENGINE_SERVICE("engine_service", "صيانة محرك"),
    TRANSMISSION_SERVICE("transmission_service", "صيانة ناقل حركة"),
    COOLING_SYSTEM("cooling_system", "نظام تبريد"),
    GENERAL_INSPECTION("general_inspection", "فحص عام"),
    REPAIR("repair", "إصلاح"),
    EMERGENCY_REPAIR("emergency_repair", "إصلاح طارئ")
}
