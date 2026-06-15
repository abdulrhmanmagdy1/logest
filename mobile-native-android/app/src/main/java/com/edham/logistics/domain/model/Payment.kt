package com.edham.logistics.domain.model

data class Payment(
    val id: String,
    val shipmentId: String,
    val amount: Double,
    val currency: String = "SAR",
    val method: String,
    val status: String,
    val transactionId: String? = null,
    val paidAt: String? = null,
    val createdAt: String,
    val updatedAt: String
)
