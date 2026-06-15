package com.edham.logistics.models

data class Report(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val date: String,
    val author: String,
    val category: String = "",
    val priority: String = "medium",
    val fileUrl: String = "",
    val fileSize: String = "",
    val downloadCount: Int = 0,
    val lastModified: String = ""
)
