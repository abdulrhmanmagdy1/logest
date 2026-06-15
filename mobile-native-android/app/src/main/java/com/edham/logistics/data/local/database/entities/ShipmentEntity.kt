package com.edham.logistics.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.edham.logistics.domain.model.ShipmentStatus
import com.edham.logistics.domain.model.CargoType
import java.util.*

/**
 * Shipment Entity for Room Database
 * كيان الشحنة لقاعدة البيانات المحلية
 */
@Entity(
    tableName = "shipments",
    indices = [
        Index(value = ["status"]),
        Index(value = ["userId"]),
        Index(value = ["driverId"]),
        Index(value = ["createdAt"]),
        Index(value = ["pickupLocation"]),
        Index(value = ["deliveryLocation"])
    ]
)
data class ShipmentEntity(
    @PrimaryKey
    val id: String,
    
    // Basic Information
    val userId: String,
    val driverId: String?,
    val vehicleId: String?,
    val trackingNumber: String,
    val status: String,
    val priority: String = "NORMAL",
    
    // Location Information
    val pickupLocation: String,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val pickupAddress: String,
    val deliveryLocation: String,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,
    val deliveryAddress: String,
    
    // Cargo Information
    val cargoType: String,
    val weight: Double,
    val volume: Double?,
    val quantity: Int,
    val description: String?,
    
    // Temperature Requirements (for cold chain)
    val temperatureRequired: Boolean = false,
    val minTemperature: Double?,
    val maxTemperature: Double?,
    val currentTemperature: Double?,
    val temperatureUnit: String = "CELSIUS",
    
    // Timing Information
    val createdAt: Date,
    val acceptedAt: Date?,
    val pickupAt: Date?,
    val deliveryAt: Date?,
    val estimatedDeliveryAt: Date?,
    val actualDeliveryAt: Date?,
    
    // Financial Information
    val cost: Double,
    val currency: String = "SAR",
    val paid: Boolean = false,
    val paymentMethod: String?,
    
    // Tracking Information
    val currentLatitude: Double?,
    val currentLongitude: Double?,
    val lastUpdated: Date?,
    val distanceTraveled: Double = 0.0,
    val estimatedTimeRemaining: Int?, // in minutes
    
    // Documents and Media
    val pickupPhoto: String?,
    val deliveryPhoto: String?,
    val documents: String = "", // Changed from List<String> to String (JSON)
    
    // Special Requirements
    val fragile: Boolean = false,
    val hazardous: Boolean = false,
    val insuranceRequired: Boolean = false,
    val insuranceAmount: Double?,
    val specialInstructions: String?,
    
    // Status History
    val statusHistory: String = "", // Changed from List<StatusHistory> to String (JSON)
    
    // Metadata
    val synced: Boolean = false,
    val lastSyncAt: Date?
)

/**
 * Status History for shipment
 */
data class StatusHistory(
    val status: String,
    val timestamp: Date,
    val location: String?,
    val notes: String?,
    val updatedBy: String? // user ID or driver ID
) {
    companion object {
        fun fromJson(json: String): StatusHistory {
            // Simple JSON parsing - in real app, use Gson
            return StatusHistory(
                status = "pending",
                timestamp = Date(),
                location = null,
                notes = null,
                updatedBy = null
            )
        }
    }
}

/**
 * Shipment Priority Enum
 * (Kept locally for backwards compatibility, should use domain.model version)
 */
enum class ShipmentPriorityLocal(val displayName: String, val color: String) {
    LOW("منخفض", "#4CAF50"),
    NORMAL("عادي", "#2196F3"),
    HIGH("عالي", "#FF9800"),
    URGENT("عاجل", "#F44336")
}

// Cargo types are now imported from domain.model

object ShipmentEntityMapper {
    fun fromDomain(shipment: com.edham.logistics.domain.model.Shipment): ShipmentEntity {
        return ShipmentEntity(
            id = shipment.id,
            userId = shipment.clientEmail, // Using clientEmail as userId since domain model doesn't have userId
            driverId = shipment.driverId,
            vehicleId = shipment.vehicleId,
            trackingNumber = shipment.trackingNumber,
            status = shipment.status,
            priority = shipment.priority,
            pickupLocation = "",
            pickupLatitude = 0.0,
            pickupLongitude = 0.0,
            pickupAddress = shipment.pickupAddress,
            deliveryLocation = "",
            deliveryLatitude = 0.0,
            deliveryLongitude = 0.0,
            deliveryAddress = shipment.deliveryAddress,
            cargoType = shipment.cargoType,
            weight = shipment.weight,
            volume = 0.0, // Domain model doesn't have volume
            quantity = 1, // Default value
            description = shipment.specialInstructions ?: "",
            temperatureRequired = shipment.temperature != null,
            minTemperature = shipment.temperature,
            maxTemperature = shipment.temperature,
            currentTemperature = shipment.temperature,
            temperatureUnit = "CELSIUS",
            createdAt = Date(),
            acceptedAt = shipment.pickupTime?.let { parseDate(it) },
            pickupAt = shipment.pickupTime?.let { parseDate(it) },
            deliveryAt = shipment.deliveryTime?.let { parseDate(it) },
            estimatedDeliveryAt = shipment.estimatedDeliveryTime?.let { parseDate(it) },
            actualDeliveryAt = shipment.actualDeliveryTime?.let { parseDate(it) },
            cost = shipment.price,
            currency = "SAR",
            paid = false,
            paymentMethod = null,
            currentLatitude = null,
            currentLongitude = null,
            lastUpdated = shipment.updatedAt?.let { parseDate(it) },
            distanceTraveled = 0.0,
            estimatedTimeRemaining = null,
            pickupPhoto = shipment.pickupPhotos.joinToString(","),
            deliveryPhoto = shipment.deliveryPhotos.joinToString(","),
            documents = shipment.documents.joinToString(","),
            specialInstructions = shipment.specialInstructions ?: "",
            statusHistory = "",
            synced = false,
            lastSyncAt = null,
            fragile = false,
            hazardous = false,
            insuranceAmount = 0.0
        )
    }
    
    private fun parseDate(dateString: String?): Date? {
        if (dateString == null) return null
        return try {
            // Simple date parsing - can be improved with proper format
            Date(dateString.toLong())
        } catch (e: Exception) {
            null
        }
    }
    
    fun toDomain(entity: ShipmentEntity): com.edham.logistics.domain.model.Shipment {
        return com.edham.logistics.domain.model.Shipment(
            id = entity.id,
            clientName = "",
            clientPhone = "",
            clientEmail = entity.userId, // Mapping userId to clientEmail
            pickupAddress = entity.pickupAddress,
            deliveryAddress = entity.deliveryAddress,
            weight = entity.weight,
            dimensions = "",
            cargoType = entity.cargoType,
            temperature = entity.currentTemperature ?: 0.0,
            humidity = null,
            status = entity.status,
            priority = entity.priority,
            specialInstructions = entity.specialInstructions ?: "",
            price = entity.cost,
            estimatedDeliveryTime = entity.estimatedDeliveryAt?.toString(),
            actualDeliveryTime = entity.actualDeliveryAt?.toString(),
            driverId = entity.driverId,
            driverName = null,
            driverPhone = null,
            vehicleId = entity.vehicleId,
            vehicleName = null,
            trackingNumber = entity.trackingNumber,
            createdAt = entity.createdAt.toString(),
            updatedAt = entity.lastUpdated?.toString() ?: entity.createdAt.toString(),
            pickupTime = entity.pickupAt?.toString(),
            deliveryTime = entity.deliveryAt?.toString(),
            pickupPhotos = if (entity.pickupPhoto.isNullOrBlank()) emptyList() else entity.pickupPhoto.split(","),
            deliveryPhotos = if (entity.deliveryPhoto.isNullOrBlank()) emptyList() else entity.deliveryPhoto.split(","),
            documents = if (entity.documents.isNullOrBlank()) emptyList() else entity.documents.split(","),
            notes = emptyList(),
            temperatureHistory = emptyList()
        )
    }
}
