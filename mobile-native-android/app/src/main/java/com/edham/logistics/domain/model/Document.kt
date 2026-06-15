package com.edham.logistics.domain.model

data class Document(
    val id: String,
    val title: String,
    val type: DocumentType,
    val template: DocumentTemplate? = null,
    val content: String,
    val metadata: DocumentMetadata,
    val status: DocumentStatus,
    val signatures: List<Signature> = emptyList(),
    val versions: List<DocumentVersion> = emptyList(),
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val fileUrl: String? = null,
    val tags: List<String> = emptyList()
)
