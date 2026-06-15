package com.edham.logistics.data.remote.dto

data class DocumentTemplateDto(
    val id: String,
    val name: String,
    val description: String,
    val fields: List<TemplateFieldDto>,
    val headerImageUrl: String? = null,
    val footerText: String? = null
)
