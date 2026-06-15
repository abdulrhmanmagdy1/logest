package com.edham.logistics.domain.model

data class Shipment(
    val id: String,
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val weight: Double,
    val dimensions: String,
    val cargoType: String,
    val temperature: Double? = null,
    val humidity: Double? = null,
    val status: String,
    val priority: String = "normal",
    val specialInstructions: String? = null,
    val price: Double,
    val estimatedDeliveryTime: String? = null,
    val actualDeliveryTime: String? = null,
    val driverId: String? = null,
    val driverName: String? = null,
    val driverPhone: String? = null,
    val vehicleId: String? = null,
    val vehicleName: String? = null,
    val trackingNumber: String,
    val createdAt: String,
    val updatedAt: String,
    val pickupTime: String? = null,
    val deliveryTime: String? = null,
    val pickupPhotos: List<String> = emptyList(),
    val deliveryPhotos: List<String> = emptyList(),
    val documents: List<String> = emptyList(),
    val notes: List<ShipmentNote> = emptyList(),
    val temperatureHistory: List<TemperatureReading> = emptyList()
)

data class ShipmentNote(
    val id: String,
    val shipmentId: String,
    val note: String,
    val createdBy: String,
    val createdAt: String
)

data class TemperatureReading(
    val id: String,
    val shipmentId: String,
    val temperature: Double,
    val humidity: Double? = null,
    val timestamp: String,
    val location: String? = null
)

enum class ShipmentStatus(val value: String, val displayName: String, val icon: String = "", val color: String = "#2196F3") {
    PENDING("pending", "قيد الانتظار", "ic_pending", "#FF9800"),
    CONFIRMED("confirmed", "مؤكد", "ic_confirmed", "#2196F3"),
    ASSIGNED("assigned", "معين لسائق", "ic_assigned", "#9C27B0"),
    PICKED_UP("picked_up", "تم الاستلام", "ic_pickup", "#4CAF50"),
    IN_TRANSIT("in_transit", "في الطريق", "ic_transit", "#2196F3"),
    AT_DELIVERY("at_delivery", "عند نقطة التسليم", "ic_at_delivery", "#673AB7"),
    DELIVERED("delivered", "تم التسليم", "ic_delivered", "#4CAF50"),
    CANCELLED("cancelled", "ملغي", "ic_cancelled", "#F44336")
}

enum class CargoType(val value: String, val displayName: String) {
    GENERAL("general", "عام"),
    FROZEN("frozen", "مجمد"),
    CHILLED("chilled", "مبرد"),
    PHARMACEUTICAL("pharmaceutical", "أدوية"),
    FOOD("food", "مواد غذائية"),
    DRY_ICE("dry_ice", "جليد جاف"),
    FLOWERS("flowers", "زهور")
}

enum class ShipmentPriority(val value: String, val displayName: String) {
    LOW("low", "منخفض"),
    NORMAL("normal", "عادي"),
    HIGH("high", "عالي"),
    URGENT("urgent", "عاجل")
}
