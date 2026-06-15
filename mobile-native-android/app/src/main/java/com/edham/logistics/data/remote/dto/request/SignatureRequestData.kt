package com.edham.logistics.data.remote.dto.request

data class SignatureRequestData(
    val recipientIds: List<String>,
    val message: String? = null,
    val deadline: String? = null  // ISO date string
)
