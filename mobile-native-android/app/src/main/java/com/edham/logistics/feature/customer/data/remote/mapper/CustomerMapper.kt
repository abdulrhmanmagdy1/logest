package com.edham.logistics.feature.customer.data.remote.mapper

import com.edham.logistics.feature.customer.data.remote.dto.CustomerShipmentDto
import com.edham.logistics.feature.customer.data.remote.dto.ShipmentCreationRequestDto
import com.edham.logistics.feature.customer.data.remote.dto.ShipmentTrackingDataDto
import com.edham.logistics.feature.customer.data.remote.dto.TrackingLocationDto
import com.edham.logistics.feature.customer.domain.model.CustomerShipment
import com.edham.logistics.feature.customer.domain.model.ShipmentCreationRequest
import com.edham.logistics.feature.customer.domain.model.ShipmentTrackingData
import com.edham.logistics.feature.customer.domain.model.TrackingLocation
import com.edham.logistics.feature.customer.domain.model.TrackingEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerMapper @Inject constructor() {
    
    fun mapToCustomerShipment(dto: CustomerShipmentDto): CustomerShipment {
        return CustomerShipment(
            id = dto.id,
            trackingNumber = dto.trackingNumber,
            cargoType = dto.cargoType,
            pickupAddress = dto.pickupAddress,
            deliveryAddress = dto.deliveryAddress,
            weight = dto.weight,
            dimensions = dto.dimensions,
            temperature = dto.temperature,
            status = dto.status,
            price = dto.price,
            createdAt = dto.createdAt,
            estimatedDeliveryTime = dto.estimatedDeliveryTime,
            pickupTime = dto.pickupTime,
            deliveryTime = dto.deliveryTime,
            driverName = dto.driverName,
            driverPhone = dto.driverPhone,
            vehicleName = dto.vehicleName,
            trackingHistory = dto.trackingHistory.map { mapToTrackingEvent(it) },
            documents = dto.documents,
            canCancel = dto.canCancel,
            canTrack = dto.canTrack,
            canModify = dto.canModify
        )
    }
    
    fun mapToShipmentCreationRequestDto(domain: ShipmentCreationRequest): ShipmentCreationRequestDto {
        return ShipmentCreationRequestDto(
            customerId = domain.customerId,
            pickupAddress = domain.pickupAddress,
            pickupLatitude = domain.pickupLatitude,
            pickupLongitude = domain.pickupLongitude,
            deliveryAddress = domain.deliveryAddress,
            deliveryLatitude = domain.deliveryLatitude,
            deliveryLongitude = domain.deliveryLongitude,
            cargoType = domain.cargoType,
            weight = domain.weight,
            notes = domain.notes,
            temperature = domain.temperature,
            priority = domain.priority,
            insuranceValue = domain.insuranceValue
        )
    }
    
    fun mapToShipmentTrackingData(dto: ShipmentTrackingDataDto): ShipmentTrackingData {
        return ShipmentTrackingData(
            shipment = mapToCustomerShipment(dto.shipment),
            currentLocation = dto.currentLocation?.let { mapToTrackingLocation(it) },
            trackingHistory = dto.trackingHistory.map { mapToTrackingEvent(it) },
            estimatedArrivalTime = dto.estimatedArrivalTime,
            isRefrigerated = dto.isRefrigerated,
            currentTemperature = dto.currentTemperature
        )
    }
    
    private fun mapToTrackingEvent(dto: com.edham.logistics.feature.customer.data.remote.dto.TrackingEventDto): TrackingEvent {
        return TrackingEvent(
            id = dto.id,
            timestamp = dto.timestamp,
            status = dto.status,
            location = dto.location,
            description = dto.description,
            latitude = dto.latitude,
            longitude = dto.longitude,
            createdBy = dto.createdBy
        )
    }
    
    private fun mapToTrackingLocation(dto: TrackingLocationDto): TrackingLocation {
        return TrackingLocation(
            latitude = dto.latitude,
            longitude = dto.longitude,
            address = dto.address,
            timestamp = dto.timestamp,
            speed = dto.speed,
            heading = dto.heading
        )
    }
    
    fun mapToCustomerShipmentList(dtos: List<CustomerShipmentDto>): List<CustomerShipment> {
        return dtos.map { mapToCustomerShipment(it) }
    }
    
    fun mapToCustomerShipmentDto(domain: CustomerShipment): CustomerShipmentDto {
        return CustomerShipmentDto(
            id = domain.id,
            trackingNumber = domain.trackingNumber,
            cargoType = domain.cargoType,
            pickupAddress = domain.pickupAddress,
            deliveryAddress = domain.deliveryAddress,
            weight = domain.weight,
            dimensions = domain.dimensions,
            temperature = domain.temperature,
            status = domain.status,
            price = domain.price,
            createdAt = domain.createdAt,
            estimatedDeliveryTime = domain.estimatedDeliveryTime,
            pickupTime = domain.pickupTime,
            deliveryTime = domain.deliveryTime,
            driverName = domain.driverName,
            driverPhone = domain.driverPhone,
            vehicleName = domain.vehicleName,
            trackingHistory = domain.trackingHistory.map { mapToTrackingEventDto(it) },
            documents = domain.documents,
            canCancel = domain.canCancel,
            canTrack = domain.canTrack,
            canModify = domain.canModify
        )
    }
    
    private fun mapToTrackingEventDto(domain: TrackingEvent): com.edham.logistics.feature.customer.data.remote.dto.TrackingEventDto {
        return com.edham.logistics.feature.customer.data.remote.dto.TrackingEventDto(
            id = domain.id,
            timestamp = domain.timestamp,
            status = domain.status,
            location = domain.location,
            description = domain.description,
            latitude = domain.latitude,
            longitude = domain.longitude,
            createdBy = domain.createdBy
        )
    }
}
