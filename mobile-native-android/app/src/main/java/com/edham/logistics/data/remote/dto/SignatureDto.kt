package com.edham.logistics.data.remote.dto

data class SignatureDto(
    val id: String,
    val userId: String,
    val userName: String,
    val signatureImage: String?,
    val signatureData: String?,
    val timestamp: String,
    val ipAddress: String? = null,
    val deviceInfo: String? = null
)
