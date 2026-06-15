package com.edham.logistics.domain.model

data class InventoryItem(
    val id: String,
    val name: String,
    val sku: String,
    val quantity: Int,
    val unit: String,
    val category: String,
    val warehouseId: String,
    val location: String,
    val reorderLevel: Int,
    val unitCost: Double,
    val createdAt: String,
    val updatedAt: String
)
