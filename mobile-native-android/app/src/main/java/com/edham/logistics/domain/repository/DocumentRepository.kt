package com.edham.logistics.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Document
import com.edham.logistics.domain.model.DocumentStatus
import com.edham.logistics.domain.model.DocumentTemplate
import com.edham.logistics.domain.model.DocumentType
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    suspend fun createDocument(request: com.edham.logistics.data.remote.dto.request.CreateDocumentRequest): Result<Document>
    suspend fun getDocument(documentId: String): Result<Document>
    suspend fun updateDocument(documentId: String, request: com.edham.logistics.data.remote.dto.request.UpdateDocumentRequest): Result<Document>
    suspend fun generatePDF(documentId: String): Result<String>
    suspend fun signDocument(documentId: String, signatureImage: String, password: String?): Result<Document>
    suspend fun listDocuments(status: DocumentStatus?, type: DocumentType?, page: Int, limit: Int): Result<List<Document>>
    suspend fun searchDocuments(query: String): Result<List<Document>>
    suspend fun deleteDocument(documentId: String): Result<Unit>
    suspend fun getTemplates(type: DocumentType?): Result<List<DocumentTemplate>>
    suspend fun shareDocument(documentId: String, recipientEmails: List<String>, message: String?): Result<Unit>
    suspend fun requestSignature(documentId: String, recipientIds: List<String>, deadline: Long?): Result<Unit>
    fun observeDocument(documentId: String): Flow<Document?>
    fun observeAllDocuments(): Flow<List<Document>>
}
