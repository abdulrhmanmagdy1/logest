package com.edham.logistics.domain.model

data class Location(
    val id: Long = 0,
    val driverId: String,
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val heading: Float = 0f,
    val timestamp: Long,
    val address: String = "",
    val batteryLevel: Int = -1,
    val networkType: String = ""
)
