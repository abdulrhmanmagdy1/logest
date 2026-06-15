package com.edham.logistics.data.remote.mapper

import com.edham.logistics.data.remote.dto.ShipmentDto
import com.edham.logistics.domain.model.Shipment
import com.edham.logistics.domain.model.ShipmentNote
import com.edham.logistics.domain.model.TemperatureReading
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipmentMapper @Inject constructor() {

    fun toDomain(dto: ShipmentDto): Shipment {
        return Shipment(
            id = dto.id,
            clientName = dto.clientName,
            clientPhone = dto.clientPhone,
            clientEmail = dto.clientEmail,
            pickupAddress = dto.pickupAddress,
            deliveryAddress = dto.deliveryAddress,
            weight = dto.weight,
            dimensions = dto.dimensions,
            cargoType = dto.cargoType,
            temperature = dto.temperature,
            humidity = dto.humidity,
            status = dto.status,
            priority = dto.priority,
            specialInstructions = dto.specialInstructions,
            price = dto.price,
            estimatedDeliveryTime = dto.estimatedDeliveryTime,
            actualDeliveryTime = dto.actualDeliveryTime,
            driverId = dto.driverId,
            driverName = dto.driverName,
            driverPhone = dto.driverPhone,
            vehicleId = dto.vehicleId,
            vehicleName = dto.vehicleName,
            trackingNumber = dto.trackingNumber,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            pickupTime = dto.pickupTime,
            deliveryTime = dto.deliveryTime,
            pickupPhotos = dto.pickupPhotos,
            deliveryPhotos = dto.deliveryPhotos,
            documents = dto.documents,
            notes = emptyList(), // Will be loaded separately
            temperatureHistory = emptyList() // Will be loaded separately
        )
    }

    fun fromDomain(domain: Shipment): ShipmentDto {
        return ShipmentDto(
            id = domain.id,
            clientName = domain.clientName,
            clientPhone = domain.clientPhone,
            clientEmail = domain.clientEmail,
            pickupAddress = domain.pickupAddress,
            deliveryAddress = domain.deliveryAddress,
            weight = domain.weight,
            dimensions = domain.dimensions,
            cargoType = domain.cargoType,
            temperature = domain.temperature,
            humidity = domain.humidity,
            status = domain.status,
            priority = domain.priority,
            specialInstructions = domain.specialInstructions,
            price = domain.price,
            estimatedDeliveryTime = domain.estimatedDeliveryTime,
            actualDeliveryTime = domain.actualDeliveryTime,
            driverId = domain.driverId,
            driverName = domain.driverName,
            driverPhone = domain.driverPhone,
            vehicleId = domain.vehicleId,
            vehicleName = domain.vehicleName,
            trackingNumber = domain.trackingNumber,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            pickupTime = domain.pickupTime,
            deliveryTime = domain.deliveryTime,
            pickupPhotos = domain.pickupPhotos,
            deliveryPhotos = domain.deliveryPhotos,
            documents = domain.documents
        )
    }

    fun toDomainList(dtos: List<ShipmentDto>): List<Shipment> {
        return dtos.map { toDomain(it) }
    }

    fun fromDomainList(domains: List<Shipment>): List<ShipmentDto> {
        return domains.map { fromDomain(it) }
    }
}
