package com.edham.logistics.domain.model

data class DocumentMetadata(
    val description: String? = null,
    val category: String? = null,
    val shipmentId: String? = null,
    val customerId: String? = null,
    val driverId: String? = null,
    val fiscalNumber: String? = null,
    val legalNotes: String? = null
)
