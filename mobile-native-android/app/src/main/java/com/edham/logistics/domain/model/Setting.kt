package com.edham.logistics.domain.model

data class Setting(
    val id: String,
    val key: String,
    val value: String,
    val type: String,
    val description: String? = null,
    val updatedAt: String
)
