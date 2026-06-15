package com.edham.logistics.feature.driver.domain.model

data class DriverProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val nationalId: String,
    val driverLicense: String,
    val licenseExpiry: String,
    val status: String,
    val rating: Float,
    val totalTrips: Int,
    val completedTrips: Int,
    val cancelledTrips: Int,
    val currentLocation: String? = null,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null,
    val currentVehicleId: String? = null,
    val currentVehicleName: String? = null,
    val currentShipmentId: String? = null,
    val isOnline: Boolean = false,
    val lastActiveTime: String? = null,
    val profileImage: String? = null,
    val hireDate: String,
    val salary: Double,
    val bankAccount: String? = null,
    val emergencyContact: EmergencyContact,
    val documents: List<DriverDocument>,
    val performanceMetrics: DriverPerformance,
    val currentShipment: ShipmentSummary? = null
)

data class DriverDocument(
    val id: String,
    val driverId: String,
    val documentType: String,
    val documentNumber: String,
    val issueDate: String,
    val expiryDate: String,
    val documentUrl: String,
    val status: String,
    val isExpired: Boolean = false
)

data class DriverPerformance(
    val averageRating: Float,
    val onTimeDeliveryRate: Float,
    val averageDeliveryTime: Int, // in minutes
    val totalDistance: Double, // in km
    val fuelEfficiency: Double, // km/l
    val customerSatisfactionScore: Float,
    val safetyScore: Float,
    val complianceScore: Float,
    val monthlyTrips: Int,
    val monthlyRevenue: Double
)

data class EmergencyContact(
    val name: String,
    val phone: String,
    val relationship: String
)

data class ShipmentSummary(
    val id: String,
    val trackingNumber: String,
    val clientName: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val status: String,
    val cargoType: String,
    val temperature: Double? = null,
    val estimatedDeliveryTime: String,
    val pickupTime: String? = null,
    val deliveryTime: String? = null,
    val price: Double,
    val distance: Double? = null,
    val duration: Int? = null // in minutes
)

data class DriverLocation(
    val driverId: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val timestamp: String,
    val speed: Float? = null, // in km/h
    val heading: Float? = null // in degrees
)

enum class DriverStatus(val value: String, val displayName: String) {
    AVAILABLE("available", "متاح"),
    ON_TRIP("on_trip", "في رحلة"),
    OFFLINE("offline", "غير متصل"),
    ON_BREAK("on_break", "في استراحة"),
    SICK_LEAVE("sick_leave", "إجازة مرضية"),
    VACATION("vacation", "إجازة"),
    SUSPENDED("suspended", "موقوف")
}

enum class DocumentType(val value: String, val displayName: String) {
    DRIVER_LICENSE("driver_license", "رخصة القيادة"),
    NATIONAL_ID("national_id", "بطاقة الهوية"),
    PASSPORT("passport", "جواز السفر"),
    MEDICAL_CERTIFICATE("medical_certificate", "شهادة طبية"),
    INSURANCE("insurance", "تأمين"),
    TRAINING_CERTIFICATE("training_certificate", "شهادة تدريب"),
    VEHICLE_REGISTRATION("vehicle_registration", "تسجيل المركبة")
}

enum class DocumentStatus(val value: String, val displayName: String) {
    VALID("valid", "ساري"),
    EXPIRED("expired", "منتهي الصلاحية"),
    PENDING("pending", "في انتظار الموافقة"),
    REJECTED("rejected", "مرفوض")
}
