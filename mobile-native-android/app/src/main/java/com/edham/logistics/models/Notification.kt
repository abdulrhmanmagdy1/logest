package com.edham.logistics.models

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: String,
    val isRead: Boolean,
    val priority: String = "medium",
    val category: String = "",
    val actionUrl: String = "",
    val imageUrl: String = ""
)
