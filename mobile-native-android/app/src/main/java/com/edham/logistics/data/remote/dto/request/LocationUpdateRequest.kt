package com.edham.logistics.data.remote.dto.request

data class LocationUpdateRequest(
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val speed: Double?,
    val heading: Double?,
    val timestamp: Long
)
