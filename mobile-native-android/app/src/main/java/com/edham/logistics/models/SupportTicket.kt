package com.edham.logistics.models

data class SupportTicket(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val time: String,
    val customerName: String,
    val customerType: String,
    val category: String = "",
    val assignedTo: String = "",
    val createdDate: String = "",
    val resolvedDate: String = "",
    val resolution: String = "",
    val tags: List<String> = emptyList()
)
