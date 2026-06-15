package com.edham.logistics.domain.model

data class DriverDocument(
    val id: String,
    val type: String,
    val url: String,
    val expiryDate: String? = null,
    val status: String = "pending"
)
