package com.edham.logistics.data.remote.dto

data class DocumentVersionDto(
    val versionNumber: Int,
    val content: String,
    val createdBy: String,
    val createdAt: String,
    val changeLog: String? = null
)
