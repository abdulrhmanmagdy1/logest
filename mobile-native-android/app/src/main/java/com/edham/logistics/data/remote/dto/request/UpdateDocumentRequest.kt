package com.edham.logistics.data.remote.dto.request

import com.edham.logistics.domain.model.DocumentMetadata

data class UpdateDocumentRequest(
    val title: String? = null,
    val content: Map<String, Any>? = null,
    val metadata: DocumentMetadata? = null
)
