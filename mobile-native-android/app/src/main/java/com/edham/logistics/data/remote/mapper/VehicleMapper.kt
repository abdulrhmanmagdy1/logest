package com.edham.logistics.data.remote.mapper

import com.edham.logistics.data.local.entity.VehicleEntity
import com.edham.logistics.data.remote.dto.VehicleDto
import com.edham.logistics.domain.model.Vehicle

object VehicleMapper {
    fun toDomain(dto: VehicleDto): Vehicle {
        return Vehicle(
            id = dto.id,
            name = dto.plateNumber,
            plateNumber = dto.plateNumber,
            make = "",
            model = "",
            year = 2024,
            type = dto.type,
            capacity = dto.capacity ?: 0.0,
            dimensions = com.edham.logistics.domain.model.VehicleDimensions(
                length = 5.0,
                width = 2.5,
                height = 3.0,
                volume = 37.5
            ),
            temperatureRange = null,
            fuelType = "diesel",
            fuelCapacity = 100.0,
            currentFuelLevel = 0.0,
            mileage = 0.0,
            status = dto.status,
            driverId = dto.driverId,
            driverName = dto.driverName,
            currentLocation = dto.currentLocation ?: "",
            currentLatitude = 0.0,
            currentLongitude = 0.0,
            lastMaintenanceDate = dto.lastMaintenanceDate ?: "",
            nextMaintenanceDate = "",
            insuranceExpiry = "",
            registrationExpiry = "",
            purchaseDate = "",
            purchasePrice = 0.0,
            currentValue = 0.0,
            basePrice = 0.0,
            pricePerKm = 0.0,
            pricePerKg = 0.0,
            features = emptyList(),
            documents = emptyList(),
            maintenanceRecords = emptyList(),
            performanceMetrics = com.edham.logistics.domain.model.VehiclePerformance(
                averageFuelConsumption = 15.0,
                totalDistance = 50000.0,
                totalTrips = 150,
                uptime = 95.0f,
                downtime = 5.0f,
                maintenanceCostPerKm = 0.5,
                fuelEfficiencyScore = 85.0f,
                reliabilityScore = 90.0f,
                lastInspectionDate = "2024-01-01"
            ),
            photos = emptyList()
        )
    }

    fun fromDomain(vehicle: Vehicle): VehicleDto {
        return VehicleDto(
            id = vehicle.id,
            plateNumber = vehicle.plateNumber,
            type = vehicle.type,
            status = vehicle.status,
            driverId = vehicle.driverId,
            driverName = vehicle.driverName,
            capacity = vehicle.capacity,
            lastMaintenanceDate = vehicle.lastMaintenanceDate,
            currentLocation = vehicle.currentLocation
        )
    }
}
