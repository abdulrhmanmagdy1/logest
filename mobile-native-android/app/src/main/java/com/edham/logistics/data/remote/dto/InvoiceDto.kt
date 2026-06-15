package com.edham.logistics.data.remote.dto

data class InvoiceDto(
    val id: String,
    val clientName: String,
    val amount: Double,
    val status: String,
    val issueDate: String,
    val dueDate: String,
    val items: List<InvoiceItemDto>? = null
)

data class InvoiceItemDto(
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val total: Double
)

data class CreateInvoiceRequest(
    val clientId: String,
    val items: List<InvoiceItemDto>,
    val dueDate: String
)
