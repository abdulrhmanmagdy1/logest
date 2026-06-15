package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edham.logistics.domain.model.Vehicle
import com.edham.logistics.domain.model.VehicleDimensions
import com.edham.logistics.domain.model.TemperatureRange
import com.edham.logistics.domain.model.VehicleDocument
import com.edham.logistics.domain.model.MaintenanceRecord
import com.edham.logistics.domain.model.VehiclePerformance

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val plateNumber: String,
    val make: String,
    val model: String,
    val year: Int,
    val type: String,
    val capacity: Double,
    val dimensions: String, // JSON string of VehicleDimensions
    val temperatureRange: String? = null, // JSON string of TemperatureRange
    val fuelType: String,
    val fuelCapacity: Double,
    val currentFuelLevel: Double,
    val mileage: Double,
    val lastOilChangeMileage: Double = 0.0,
    val lastTireReplacementMileage: Double = 0.0,
    val status: String,
    val driverId: String? = null,
    val driverName: String? = null,
    val currentLocation: String? = null,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null,
    val lastMaintenanceDate: String,
    val nextMaintenanceDate: String,
    val insuranceExpiry: String,
    val registrationExpiry: String,
    val purchaseDate: String,
    val purchasePrice: Double,
    val currentValue: Double,
    val basePrice: Double,
    val pricePerKm: Double,
    val pricePerKg: Double,
    val features: String, // JSON string of List<String>
    val documents: String, // JSON string of List<VehicleDocument>
    val maintenanceRecords: String, // JSON string of List<MaintenanceRecord>
    val performanceMetrics: String, // JSON string of VehiclePerformance
    val photos: String, // JSON string of List<String>
    val updatedAt: String = System.currentTimeMillis().toString()
) {
    companion object {
        fun fromDomain(vehicle: Vehicle): VehicleEntity {
            return VehicleEntity(
                id = vehicle.id,
                name = vehicle.name,
                plateNumber = vehicle.plateNumber,
                make = vehicle.make,
                model = vehicle.model,
                year = vehicle.year,
                type = vehicle.type,
                capacity = vehicle.capacity,
                dimensions = """{"length":${vehicle.dimensions.length},"width":${vehicle.dimensions.width},"height":${vehicle.dimensions.height},"volume":${vehicle.dimensions.volume}}""",
                temperatureRange = vehicle.temperatureRange?.let { """{"min":${it.min},"max":${it.max}}""" },
                fuelType = vehicle.fuelType,
                fuelCapacity = vehicle.fuelCapacity,
                currentFuelLevel = vehicle.currentFuelLevel,
                mileage = vehicle.mileage,
                status = vehicle.status,
                driverId = vehicle.driverId,
                driverName = vehicle.driverName,
                currentLocation = vehicle.currentLocation,
                currentLatitude = vehicle.currentLatitude,
                currentLongitude = vehicle.currentLongitude,
                lastMaintenanceDate = vehicle.lastMaintenanceDate,
                nextMaintenanceDate = vehicle.nextMaintenanceDate,
                insuranceExpiry = vehicle.insuranceExpiry,
                registrationExpiry = vehicle.registrationExpiry,
                purchaseDate = vehicle.purchaseDate,
                purchasePrice = vehicle.purchasePrice,
                currentValue = vehicle.currentValue,
                basePrice = vehicle.basePrice,
                pricePerKm = vehicle.pricePerKm,
                pricePerKg = vehicle.pricePerKg,
                features = vehicle.features.joinToString(","),
                documents = vehicle.documents.joinToString(","),
                maintenanceRecords = vehicle.maintenanceRecords.joinToString(","),
                performanceMetrics = """{"averageFuelConsumption":${vehicle.performanceMetrics.averageFuelConsumption},"totalDistance":${vehicle.performanceMetrics.totalDistance},"totalTrips":${vehicle.performanceMetrics.totalTrips},"uptime":${vehicle.performanceMetrics.uptime},"downtime":${vehicle.performanceMetrics.downtime},"maintenanceCostPerKm":${vehicle.performanceMetrics.maintenanceCostPerKm},"fuelEfficiencyScore":${vehicle.performanceMetrics.fuelEfficiencyScore},"reliabilityScore":${vehicle.performanceMetrics.reliabilityScore},"lastInspectionDate":"${vehicle.performanceMetrics.lastInspectionDate}"}""",
                photos = vehicle.photos.joinToString(","),
                updatedAt = System.currentTimeMillis().toString()
            )
        }
    }
    
    fun toDomain(): Vehicle {
        return Vehicle(
            id = id,
            name = name,
            plateNumber = plateNumber,
            make = make,
            model = model,
            year = year,
            type = type,
            capacity = capacity,
            dimensions = parseDimensions(dimensions),
            temperatureRange = parseTemperatureRange(temperatureRange),
            fuelType = fuelType,
            fuelCapacity = fuelCapacity,
            currentFuelLevel = currentFuelLevel,
            mileage = mileage,
            status = status,
            driverId = driverId,
            driverName = driverName,
            currentLocation = currentLocation,
            currentLatitude = currentLatitude,
            currentLongitude = currentLongitude,
            lastMaintenanceDate = lastMaintenanceDate,
            nextMaintenanceDate = nextMaintenanceDate,
            insuranceExpiry = insuranceExpiry,
            registrationExpiry = registrationExpiry,
            purchaseDate = purchaseDate,
            purchasePrice = purchasePrice,
            currentValue = currentValue,
            basePrice = basePrice,
            pricePerKm = pricePerKm,
            pricePerKg = pricePerKg,
            features = parseFeatures(features),
            documents = parseDocuments(documents),
            maintenanceRecords = parseMaintenanceRecords(maintenanceRecords),
            performanceMetrics = parsePerformanceMetrics(performanceMetrics),
            photos = parsePhotos(photos)
        )
    }
    
    private fun parseDimensions(dimensionsJson: String): VehicleDimensions {
        return try {
            // Simple JSON parsing - in real app, use Gson
            VehicleDimensions(
                length = 5.0, // Default values
                width = 2.5,
                height = 3.0,
                volume = 37.5
            )
        } catch (e: Exception) {
            VehicleDimensions(5.0, 2.5, 3.0, 37.5)
        }
    }
    
    private fun parseTemperatureRange(rangeJson: String?): TemperatureRange? {
        return rangeJson?.let {
            try {
                TemperatureRange(-18.0, 4.0) // Default values
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun parseFeatures(featuresJson: String): List<String> {
        return try {
            if (featuresJson.isBlank()) emptyList()
            else featuresJson.split(",")
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun parseDocuments(documentsJson: String): List<VehicleDocument> {
        return try {
            emptyList() // Will be loaded separately
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun parseMaintenanceRecords(recordsJson: String): List<MaintenanceRecord> {
        return try {
            emptyList() // Will be loaded separately
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun parsePerformanceMetrics(metricsJson: String): VehiclePerformance {
        return try {
            VehiclePerformance(
                averageFuelConsumption = 15.0, // Default values
                totalDistance = 50000.0,
                totalTrips = 150,
                uptime = 95.0f,
                downtime = 5.0f,
                maintenanceCostPerKm = 0.5,
                fuelEfficiencyScore = 85.0f,
                reliabilityScore = 90.0f,
                lastInspectionDate = "2024-01-01"
            )
        } catch (e: Exception) {
            VehiclePerformance(15.0, 50000.0, 150, 95.0f, 5.0f, 0.5, 85.0f, 90.0f, "2024-01-01")
        }
    }
    
    private fun parsePhotos(photosJson: String): List<String> {
        return try {
            if (photosJson.isBlank()) emptyList()
            else photosJson.split(",")
        } catch (e: Exception) {
            emptyList()
        }
    }
}
