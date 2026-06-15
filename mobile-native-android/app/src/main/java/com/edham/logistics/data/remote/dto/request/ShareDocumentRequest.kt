package com.edham.logistics.data.remote.dto.request

data class ShareDocumentRequest(
    val recipientEmails: List<String>,
    val message: String? = null,
    val expiresIn: Long? = null  // milliseconds
)
