package com.edham.logistics.data.remote.dto.request

import com.edham.logistics.domain.model.DocumentMetadata

data class CreateDocumentRequest(
    val title: String,
    val type: String,
    val templateId: String? = null,
    val content: Map<String, Any>,
    val metadata: DocumentMetadata,
    val shipmentId: String? = null
)
