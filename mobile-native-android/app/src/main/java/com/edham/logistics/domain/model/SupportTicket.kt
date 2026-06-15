package com.edham.logistics.domain.model

data class SupportTicket(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val createdBy: String,
    val assignedTo: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val messages: List<String> = emptyList()
)
