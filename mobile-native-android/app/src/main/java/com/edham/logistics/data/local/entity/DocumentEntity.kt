package com.edham.logistics.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: String,
    val templateId: String?,
    val content: String,
    val metadata: String,  // JSON string
    val status: String,
    val signatures: String,  // JSON string
    val versions: String,  // JSON string
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val fileUrl: String?,
    val tags: String  // JSON array string
)
