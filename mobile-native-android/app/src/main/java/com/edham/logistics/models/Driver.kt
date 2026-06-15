package com.edham.logistics.models

data class Driver(
    val id: String,
    val name: String,
    val vehicleNumber: String,
    val status: String,
    val currentTask: String,
    val phoneNumber: String,
    val email: String = "",
    val rating: Float = 0.0f,
    val totalTrips: Int = 0,
    val earnings: Double = 0.0,
    val location: String = "",
    val lastActive: String = ""
)
