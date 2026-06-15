package com.edham.logistics.data.remote.dto

data class TemplateFieldDto(
    val id: String,
    val name: String,
    val label: String,
    val type: String,
    val required: Boolean,
    val placeholder: String? = null,
    val options: List<String>? = null
)
