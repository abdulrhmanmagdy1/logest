package com.edham.logistics.domain.model

data class Signature(
    val id: String,
    val userId: String,
    val userName: String,
    val signatureImage: String?,  // Base64 encoded image
    val signatureData: String?,       // Digital signature data
    val timestamp: Long,
    val ipAddress: String? = null,
    val deviceInfo: String? = null
)
