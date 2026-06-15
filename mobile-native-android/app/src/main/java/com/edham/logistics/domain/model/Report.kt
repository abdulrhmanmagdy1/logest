package com.edham.logistics.domain.model

data class Report(
    val id: String,
    val title: String,
    val type: String,
    val generatedBy: String,
    val content: String,
    val format: String,
    val createdAt: String,
    val updatedAt: String
)
