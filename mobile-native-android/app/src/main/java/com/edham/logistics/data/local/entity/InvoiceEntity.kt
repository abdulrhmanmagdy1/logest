package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey
    val id: String,
    val shipmentId: String,
    val customerId: String,
    val amount: Double,
    val currency: String = "SAR",
    val status: String, // pending, paid, cancelled
    val issuedDate: Date,
    val dueDate: Date,
    val paidDate: Date?,
    val paymentMethod: String?,
    val description: String?,
    val items: String, // JSON string
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val createdAt: Date,
    val updatedAt: Date?
)
