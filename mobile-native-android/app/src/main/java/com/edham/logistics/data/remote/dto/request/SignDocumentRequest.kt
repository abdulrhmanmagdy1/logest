package com.edham.logistics.data.remote.dto.request

data class SignDocumentRequest(
    val signatureImage: String,  // Base64
    val signatureData: String? = null,
    val password: String? = null
)
