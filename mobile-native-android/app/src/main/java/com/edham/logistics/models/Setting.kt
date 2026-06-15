package com.edham.logistics.models

data class Setting(
    val id: String,
    val title: String,
    val description: String,
    val value: Any,
    val type: String,
    val category: String = "",
    val isRequired: Boolean = false,
    val minValue: Int = 0,
    val maxValue: Int = 100,
    val options: List<String> = emptyList()
)
