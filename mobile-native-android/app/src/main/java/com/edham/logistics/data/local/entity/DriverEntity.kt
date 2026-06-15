package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey
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
    val currentShipmentId: String? = null,
    val isOnline: Boolean = false,
    val lastActiveTime: String? = null,
    val profileImage: String? = null,
    val hireDate: String,
    val salary: Double,
    val bankAccount: String? = null,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val emergencyContactRelationship: String
)

object DriverEntityMapper {
    fun fromDomain(driver: com.edham.logistics.domain.model.Driver): DriverEntity {
        return DriverEntity(
            id = driver.id,
            name = driver.name,
            email = driver.email,
            phone = driver.phone,
            nationalId = driver.nationalId,
            driverLicense = driver.driverLicense,
            licenseExpiry = driver.licenseExpiry,
            status = driver.status,
            rating = driver.rating,
            totalTrips = driver.totalTrips,
            completedTrips = driver.completedTrips,
            cancelledTrips = driver.cancelledTrips,
            currentLocation = driver.currentLocation,
            currentLatitude = driver.currentLatitude,
            currentLongitude = driver.currentLongitude,
            currentVehicleId = driver.currentVehicleId,
            currentShipmentId = driver.currentShipmentId,
            isOnline = driver.isOnline,
            lastActiveTime = driver.lastActiveTime,
            profileImage = driver.profileImage,
            hireDate = driver.hireDate,
            salary = driver.salary,
            bankAccount = driver.bankAccount,
            emergencyContactName = driver.emergencyContact.name,
            emergencyContactPhone = driver.emergencyContact.phone,
            emergencyContactRelationship = driver.emergencyContact.relationship
        )
    }
    
    fun toDomain(entity: DriverEntity): com.edham.logistics.domain.model.Driver {
        return com.edham.logistics.domain.model.Driver(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            nationalId = entity.nationalId,
            driverLicense = entity.driverLicense,
            licenseExpiry = entity.licenseExpiry,
            status = entity.status,
            rating = entity.rating,
            totalTrips = entity.totalTrips,
            completedTrips = entity.completedTrips,
            cancelledTrips = entity.cancelledTrips,
            currentLocation = entity.currentLocation,
            currentLatitude = entity.currentLatitude,
            currentLongitude = entity.currentLongitude,
            currentVehicleId = entity.currentVehicleId,
            currentShipmentId = entity.currentShipmentId,
            isOnline = entity.isOnline,
            lastActiveTime = entity.lastActiveTime,
            profileImage = entity.profileImage,
            hireDate = entity.hireDate,
            salary = entity.salary,
            bankAccount = entity.bankAccount,
            emergencyContact = com.edham.logistics.domain.model.EmergencyContact(
                name = entity.emergencyContactName,
                phone = entity.emergencyContactPhone,
                relationship = entity.emergencyContactRelationship
            ),
            documents = emptyList(),
            performanceMetrics = com.edham.logistics.domain.model.DriverPerformance(
                averageRating = entity.rating,
                onTimeDeliveryRate = 0.0f,
                averageDeliveryTime = 0,
                totalDistance = 0.0,
                fuelEfficiency = 0.0,
                customerSatisfactionScore = entity.rating,
                safetyScore = 0.0f,
                complianceScore = 0.0f
            )
        )
    }
}
