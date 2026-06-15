package com.edham.logistics.domain.model

data class DocumentVersion(
    val versionNumber: Int,
    val content: String,
    val createdBy: String,
    val createdAt: Long,
    val changeLog: String? = null
)
