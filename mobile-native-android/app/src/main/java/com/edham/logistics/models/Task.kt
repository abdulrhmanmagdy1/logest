package com.edham.logistics.models

data class Task(
    val id: String,
    val title: String,
    val assignedTo: String,
    val status: String,
    val priority: String,
    val time: String,
    val description: String = "",
    val category: String = "",
    val estimatedDuration: String = "",
    val actualDuration: String = "",
    val dueDate: String = "",
    val completedDate: String = "",
    val notes: String = ""
)
