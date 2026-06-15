package com.edham.logistics.feature.customer.domain.model

data class CustomerProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val companyName: String? = null,
    val companyType: String? = null,
    val taxNumber: String? = null,
    val commercialRegister: String? = null,
    val address: CustomerAddress,
    val status: String,
    val registrationDate: String,
    val lastLogin: String? = null,
    val profileImage: String? = null,
    val preferences: CustomerPreferences,
    val statistics: CustomerStatistics
)

data class CustomerAddress(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = true
)

data class CustomerPreferences(
    val language: String = "ar",
    val currency: String = "SAR",
    val notifications: NotificationPreferences,
    val privacy: PrivacyPreferences
)

data class NotificationPreferences(
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val shipmentUpdates: Boolean = true,
    val deliveryUpdates: Boolean = true,
    val paymentReminders: Boolean = true,
    val promotionalEmails: Boolean = false
)

data class PrivacyPreferences(
    val shareContactInfo: Boolean = false,
    val allowLocationTracking: Boolean = true,
    val dataCollection: Boolean = true,
    val marketingConsent: Boolean = false
)

data class CustomerStatistics(
    val totalShipments: Int,
    val completedShipments: Int,
    val cancelledShipments: Int,
    val totalSpent: Double,
    val averageShipmentCost: Double,
    val favoriteCargoType: String,
    val mostUsedDestination: String,
    val loyaltyPoints: Int,
    val membershipTier: String,
    val nextTierPoints: Int,
    val lastShipmentDate: String? = null
)

data class CustomerShipment(
    val id: String,
    val trackingNumber: String,
    val cargoType: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val weight: Double,
    val dimensions: String,
    val temperature: Double? = null,
    val status: String,
    val price: Double,
    val createdAt: String,
    val estimatedDeliveryTime: String,
    val actualDeliveryTime: String? = null,
    val pickupTime: String? = null,
    val deliveryTime: String? = null,
    val driverName: String? = null,
    val driverPhone: String? = null,
    val vehicleName: String? = null,
    val trackingHistory: List<TrackingEvent> = emptyList(),
    val documents: List<String> = emptyList(),
    val canCancel: Boolean = false,
    val canTrack: Boolean = true,
    val canModify: Boolean = false
)

data class TrackingEvent(
    val id: String,
    val timestamp: String,
    val status: String,
    val location: String,
    val description: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdBy: String? = null
)

data class ShipmentRequest(
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val cargoType: String,
    val weight: Double,
    val dimensions: String,
    val temperature: Double? = null,
    val specialInstructions: String? = null,
    val priority: String = "normal",
    val insuranceValue: Double? = null,
    val paymentMethod: String = "cash",
    val scheduledPickupTime: String? = null
)

enum class CustomerStatus(val value: String, val displayName: String) {
    ACTIVE("active", "نشط"),
    INACTIVE("inactive", "غير نشط"),
    SUSPENDED("suspended", "موقوف"),
    PENDING("pending", "في انتظار الموافقة"),
    VERIFIED("verified", "موثق"),
    UNVERIFIED("unverified", "غير موثق")
}

enum class CompanyType(val value: String, val displayName: String) {
    INDIVIDUAL("individual", "فردي"),
    SMALL_BUSINESS("small_business", "مشروع صغير"),
    MEDIUM_BUSINESS("medium_business", "مشروع متوسط"),
    LARGE_BUSINESS("large_business", "مشروع كبير"),
    CORPORATION("corporation", "شركة"),
    GOVERNMENT("government", "حكومي"),
    NGO("ngo", "منظمة غير حكومية")
}

enum class MembershipTier(val value: String, val displayName: String, val minPoints: Int) {
    BRONZE("bronze", "برونزي", 0),
    SILVER("silver", "فضي", 1000),
    GOLD("gold", "ذهبي", 5000),
    PLATINUM("platinum", "بلاتيني", 15000),
    DIAMOND("diamond", "ماسي", 50000)
}
