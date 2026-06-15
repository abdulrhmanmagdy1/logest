package com.edham.logistics.models

data class SecuritySetting(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val category: String,
    val isEnabled: Boolean = false,
    val lastUpdated: String = "",
    val requiredLevel: String = "standard",
    val options: List<String> = emptyList()
)
