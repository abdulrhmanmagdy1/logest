package com.edham.logistics.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val shipmentId: String,
    val amount: Double,
    val method: String,
    val status: String
)
