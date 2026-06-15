package com.edham.logistics.domain.model

data class FuelTransactionEntity(
    val id: String,
    val vehicleId: String,
    val fuelType: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalCost: Double,
    val transactionDate: String,
    val mileage: Int,
    val notes: String? = null
)
