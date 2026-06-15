package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spare_parts")
data class SparePartEntity(
    @PrimaryKey val partId: String = "",
    val name: String = "",
    val description: String = "",
    val quantity: Int = 0,
    val minStockLevel: Int = 0,
    val maxStockLevel: Int = 0,
    val unitPrice: Float = 0f,
    val supplier: String = "",
    val category: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
