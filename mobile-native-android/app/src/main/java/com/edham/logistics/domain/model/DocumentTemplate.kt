package com.edham.logistics.domain.model

data class DocumentTemplate(
    val id: String,
    val name: String,
    val description: String,
    val fields: List<TemplateField>,
    val headerImageUrl: String? = null,
    val footerText: String? = null
)
