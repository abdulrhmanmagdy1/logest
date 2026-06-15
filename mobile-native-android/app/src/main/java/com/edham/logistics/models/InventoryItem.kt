package com.edham.logistics.models

data class InventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val minStock: Int,
    val status: String,
    val sku: String = "",
    val description: String = "",
    val supplier: String = "",
    val lastUpdated: String = "",
    val location: String = "",
    val unit: String = "قطعة"
)
