package com.edham.logistics.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val assignedTo: String,
    val dueDate: String,
    val createdAt: String,
    val updatedAt: String
)
