package com.edham.logistics.models

data class Shipment(
    val id: String,
    val trackingNumber: String,
    val origin: String,
    val destination: String,
    val status: String,
    val driverName: String,
    val vehicleNumber: String,
    val time: String,
    val weight: Double = 0.0,
    val value: Double = 0.0,
    val priority: String = "عادي",
    val estimatedDelivery: String = "",
    val actualDelivery: String = "",
    val notes: String = ""
)
