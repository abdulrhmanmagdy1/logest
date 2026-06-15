package com.edham.logistics.models

data class Payment(
    val id: String,
    val title: String,
    val amount: Double,
    val status: String,
    val time: String,
    val customerName: String,
    val paymentMethod: String,
    val shipmentId: String = "",
    val transactionId: String = "",
    val invoiceId: String = "",
    val dueDate: String = "",
    val paidDate: String = "",
    val notes: String = ""
)
