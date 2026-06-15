package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edham.logistics.domain.model.Shipment
import com.edham.logistics.domain.model.ShipmentStatus
import com.edham.logistics.domain.model.CargoType
import com.edham.logistics.domain.model.ShipmentPriority

@Entity(tableName = "shipments")
data class ShipmentEntity(
    @PrimaryKey
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
    val documents: List<String> = emptyList()
) {
    companion object {
        fun fromDomain(shipment: Shipment): ShipmentEntity {
            return ShipmentEntity(
                id = shipment.id,
                clientName = shipment.clientName,
                clientPhone = shipment.clientPhone,
                clientEmail = shipment.clientEmail,
                pickupAddress = shipment.pickupAddress,
                deliveryAddress = shipment.deliveryAddress,
                weight = shipment.weight,
                dimensions = shipment.dimensions,
                cargoType = shipment.cargoType,
                temperature = shipment.temperature,
                humidity = shipment.humidity,
                status = shipment.status,
                priority = shipment.priority,
                specialInstructions = shipment.specialInstructions,
                price = shipment.price,
                estimatedDeliveryTime = shipment.estimatedDeliveryTime,
                actualDeliveryTime = shipment.actualDeliveryTime,
                driverId = shipment.driverId,
                driverName = shipment.driverName,
                driverPhone = shipment.driverPhone,
                vehicleId = shipment.vehicleId,
                vehicleName = shipment.vehicleName,
                trackingNumber = shipment.trackingNumber,
                createdAt = shipment.createdAt,
                updatedAt = shipment.updatedAt,
                pickupTime = shipment.pickupTime,
                deliveryTime = shipment.deliveryTime,
                pickupPhotos = shipment.pickupPhotos,
                deliveryPhotos = shipment.deliveryPhotos,
                documents = shipment.documents
            )
        }
    }
    
    fun toDomain(): Shipment {
        return Shipment(
            id = id,
            clientName = clientName,
            clientPhone = clientPhone,
            clientEmail = clientEmail,
            pickupAddress = pickupAddress,
            deliveryAddress = deliveryAddress,
            weight = weight,
            dimensions = dimensions,
            cargoType = cargoType,
            temperature = temperature,
            humidity = humidity,
            status = status,
            priority = priority,
            specialInstructions = specialInstructions,
            price = price,
            estimatedDeliveryTime = estimatedDeliveryTime,
            actualDeliveryTime = actualDeliveryTime,
            driverId = driverId,
            driverName = driverName,
            driverPhone = driverPhone,
            vehicleId = vehicleId,
            vehicleName = vehicleName,
            trackingNumber = trackingNumber,
            createdAt = createdAt,
            updatedAt = updatedAt,
            pickupTime = pickupTime,
            deliveryTime = deliveryTime,
            pickupPhotos = pickupPhotos,
            deliveryPhotos = deliveryPhotos,
            documents = documents,
            notes = emptyList(), // Will be loaded separately
            temperatureHistory = emptyList() // Will be loaded separately
        )
    }
}
