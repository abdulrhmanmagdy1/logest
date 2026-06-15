package com.edham.logistics.data.remote.dto

import com.edham.logistics.domain.model.DocumentMetadata
import com.edham.logistics.domain.model.DocumentStatus
import com.edham.logistics.domain.model.DocumentType

data class DocumentDto(
    val id: String,
    val title: String,
    val type: String,
    val template: DocumentTemplateDto? = null,
    val content: String,
    val metadata: DocumentMetadata,
    val status: String,
    val signatures: List<SignatureDto> = emptyList(),
    val versions: List<DocumentVersionDto> = emptyList(),
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val fileUrl: String? = null,
    val tags: List<String> = emptyList()
)
