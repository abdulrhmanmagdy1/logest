package com.edham.logistics.domain.model

data class SecuritySetting(
    val id: String,
    val key: String,
    val value: String,
    val type: String,
    val required: Boolean = false,
    val description: String? = null,
    val updatedAt: String
)
