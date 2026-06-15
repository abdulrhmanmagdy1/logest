package com.edham.logistics.data.remote.mapper

import com.edham.logistics.data.remote.dto.DriverDto
import com.edham.logistics.data.remote.dto.EmergencyContactDto
import com.edham.logistics.domain.model.Driver
import com.edham.logistics.domain.model.EmergencyContact
import com.edham.logistics.domain.model.DriverDocument
import com.edham.logistics.domain.model.DriverPerformance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverMapper @Inject constructor() {

    fun toDomain(dto: DriverDto): Driver {
        return Driver(
            id = dto.id,
            name = dto.name,
            email = dto.email,
            phone = dto.phone,
            nationalId = dto.nationalId,
            driverLicense = dto.driverLicense,
            licenseExpiry = dto.licenseExpiry,
            status = dto.status,
            rating = dto.rating,
            totalTrips = dto.totalTrips,
            completedTrips = dto.completedTrips,
            cancelledTrips = dto.cancelledTrips,
            currentLocation = dto.currentLocation,
            currentLatitude = dto.currentLatitude,
            currentLongitude = dto.currentLongitude,
            currentVehicleId = dto.currentVehicleId,
            currentShipmentId = dto.currentShipmentId,
            isOnline = dto.isOnline,
            lastActiveTime = dto.lastActiveTime,
            profileImage = dto.profileImage,
            hireDate = dto.hireDate,
            salary = dto.salary,
            bankAccount = dto.bankAccount,
            emergencyContact = EmergencyContact(
                name = dto.emergencyContact.name,
                phone = dto.emergencyContact.phone,
                relationship = dto.emergencyContact.relationship
            ),
            documents = emptyList(), // Will be loaded separately
            performanceMetrics = DriverPerformance(
                averageRating = dto.rating,
                onTimeDeliveryRate = 0.0f, // Will be calculated
                averageDeliveryTime = 0, // Will be calculated
                totalDistance = 0.0, // Will be calculated
                fuelEfficiency = 0.0, // Will be calculated
                customerSatisfactionScore = dto.rating,
                safetyScore = 0.0f, // Will be calculated
                complianceScore = 0.0f // Will be calculated
            )
        )
    }

    fun fromDomain(domain: Driver): DriverDto {
        return DriverDto(
            id = domain.id,
            name = domain.name,
            email = domain.email,
            phone = domain.phone,
            nationalId = domain.nationalId,
            driverLicense = domain.driverLicense,
            licenseExpiry = domain.licenseExpiry,
            status = domain.status,
            rating = domain.rating,
            totalTrips = domain.totalTrips,
            completedTrips = domain.completedTrips,
            cancelledTrips = domain.cancelledTrips,
            currentLocation = domain.currentLocation,
            currentLatitude = domain.currentLatitude,
            currentLongitude = domain.currentLongitude,
            currentVehicleId = domain.currentVehicleId,
            currentShipmentId = domain.currentShipmentId,
            isOnline = domain.isOnline,
            lastActiveTime = domain.lastActiveTime,
            profileImage = domain.profileImage,
            hireDate = domain.hireDate,
            salary = domain.salary,
            bankAccount = domain.bankAccount,
            emergencyContact = EmergencyContactDto(
                name = domain.emergencyContact.name,
                phone = domain.emergencyContact.phone,
                relationship = domain.emergencyContact.relationship
            )
        )
    }

    fun toDomainList(dtos: List<DriverDto>): List<Driver> {
        return dtos.map { toDomain(it) }
    }

    fun fromDomainList(domains: List<Driver>): List<DriverDto> {
        return domains.map { fromDomain(it) }
    }
}
