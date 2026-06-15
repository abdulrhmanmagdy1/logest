package com.edham.logistics.data.remote.mapper

import com.edham.logistics.data.local.entity.LocationEntity
import com.edham.logistics.domain.model.Location
import java.util.Date

object LocationMapper {
    
    fun toDomain(entity: LocationEntity, driverId: String): Location {
        return Location(
            id = entity.id,
            driverId = driverId,
            vehicleId = entity.vehicleId,
            latitude = entity.latitude,
            longitude = entity.longitude,
            altitude = entity.altitude,
            accuracy = 0f, // Not stored in LocationEntity
            speed = entity.speed.toFloat(),
            heading = entity.heading.toFloat(),
            timestamp = entity.timestamp.time,
            address = "", // Not stored in LocationEntity
            batteryLevel = -1, // Not stored in LocationEntity
            networkType = "" // Not stored in LocationEntity
        )
    }
    
    fun toEntity(location: Location): LocationEntity {
        return LocationEntity(
            id = location.id,
            vehicleId = location.vehicleId,
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            speed = location.speed.toDouble(),
            heading = location.heading.toDouble(),
            timestamp = Date(location.timestamp)
        )
    }
}
