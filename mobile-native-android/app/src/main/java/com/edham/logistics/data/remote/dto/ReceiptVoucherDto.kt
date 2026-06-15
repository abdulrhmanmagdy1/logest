package com.edham.logistics.data.remote.dto

data class ReceiptVoucherDto(
    val id: String,
    val invoiceId: String,
    val clientName: String,
    val amount: Double,
    val method: String,
    val status: String,
    val date: String,
    val notes: String? = null,
    val attachmentUrl: String? = null
)

data class CreateReceiptVoucherRequest(
    val invoiceId: String,
    val amount: Double,
    val method: String,
    val notes: String? = null
)

data class ReceiptVoucherStatsResponse(
    val totalReceipts: Int,
    val totalAmount: Double,
    val pendingAmount: Double,
    val paidAmount: Double
)
