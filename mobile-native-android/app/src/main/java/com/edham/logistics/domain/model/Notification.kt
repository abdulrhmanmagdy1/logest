package com.edham.logistics.domain.model

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val userId: String,
    val isRead: Boolean = false,
    val data: Map<String, String> = emptyMap(),
    val createdAt: String
)
