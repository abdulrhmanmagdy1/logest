package com.edham.logistics.domain.model

data class TemplateField(
    val id: String,
    val name: String,
    val label: String,
    val type: FieldType,
    val required: Boolean,
    val placeholder: String? = null,
    val options: List<String>? = null
)
