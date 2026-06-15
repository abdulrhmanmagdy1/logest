package com.edham.logistics.data.repository

import com.edham.logistics.core.network.ApiResponse
import com.edham.logistics.core.network.DocumentApiService
import com.edham.logistics.core.utils.Result
import com.edham.logistics.data.local.database.dao.DocumentDao
import com.edham.logistics.data.local.entity.DocumentEntity
import com.edham.logistics.data.remote.mapper.DocumentMapper
import com.edham.logistics.domain.model.Document
import com.edham.logistics.domain.model.DocumentStatus
import com.edham.logistics.domain.model.DocumentTemplate
import com.edham.logistics.domain.model.DocumentType
import com.edham.logistics.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentApiService: DocumentApiService,
    private val documentDao: DocumentDao
) : DocumentRepository {

    override suspend fun createDocument(request: com.edham.logistics.data.remote.dto.request.CreateDocumentRequest): Result<Document> {
        return try {
            val response = documentApiService.createDocument(request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    val document = DocumentMapper.toDomain(apiResponse.data)
                    documentDao.insertDocument(DocumentMapper.toEntity(document))
                    Result.Success(document)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Unknown error"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getDocument(documentId: String): Result<Document> {
        return try {
            // Try local first
            val local = documentDao.getDocument(documentId)
            if (local != null) {
                return Result.Success(DocumentMapper.toDomain(local))
            }

            // Fetch from server
            val response = documentApiService.getDocument(documentId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    val document = DocumentMapper.toDomain(apiResponse.data)
                    documentDao.insertDocument(DocumentMapper.toEntity(document))
                    Result.Success(document)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Unknown error"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateDocument(documentId: String, request: com.edham.logistics.data.remote.dto.request.UpdateDocumentRequest): Result<Document> {
        return try {
            val response = documentApiService.updateDocument(documentId, request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    val document = DocumentMapper.toDomain(apiResponse.data)
                    documentDao.updateDocument(DocumentMapper.toEntity(document))
                    Result.Success(document)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Unknown error"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun generatePDF(documentId: String): Result<String> {
        return try {
            val response = documentApiService.generatePDF(documentId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data.url)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Failed to generate PDF"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signDocument(documentId: String, signatureImage: String, password: String?): Result<Document> {
        return try {
            val request = com.edham.logistics.data.remote.dto.request.SignDocumentRequest(
                signatureImage = signatureImage,
                signatureData = null,
                password = password
            )
            val response = documentApiService.signDocument(documentId, request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    val document = DocumentMapper.toDomain(apiResponse.data)
                    documentDao.updateDocument(DocumentMapper.toEntity(document))
                    Result.Success(document)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Failed to sign document"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun listDocuments(status: DocumentStatus?, type: DocumentType?, page: Int, limit: Int): Result<List<Document>> {
        return try {
            val response = documentApiService.listDocuments(
                status = status?.name,
                type = type?.name,
                page = page,
                limit = limit
            )
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    val documents = apiResponse.data.map { DocumentMapper.toDomain(it) }
                    documentDao.insertDocuments(documents.map { DocumentMapper.toEntity(it) })
                    Result.Success(documents)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Unknown error"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchDocuments(query: String): Result<List<Document>> {
        return try {
            val response = documentApiService.searchDocuments(query, null)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data.map { DocumentMapper.toDomain(it) })
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Search failed"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteDocument(documentId: String): Result<Unit> {
        return try {
            val response = documentApiService.deleteDocument(documentId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    documentDao.deleteDocumentById(documentId)
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Delete failed"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTemplates(type: DocumentType?): Result<List<DocumentTemplate>> {
        return try {
            val response = documentApiService.getTemplates(type?.name)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data.map { DocumentMapper.toDomainTemplate(it) })
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Failed to get templates"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun shareDocument(documentId: String, recipientEmails: List<String>, message: String?): Result<Unit> {
        return try {
            val request = com.edham.logistics.data.remote.dto.request.ShareDocumentRequest(
                recipientEmails = recipientEmails,
                message = message
            )
            val response = documentApiService.shareDocument(documentId, request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Share failed"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun requestSignature(documentId: String, recipientIds: List<String>, deadline: Long?): Result<Unit> {
        return try {
            val deadlineString = deadline?.let { java.time.Instant.ofEpochMilli(it).toString() }
            val request = com.edham.logistics.data.remote.dto.request.SignatureRequestData(
                recipientIds = recipientIds,
                deadline = deadlineString
            )
            val response = documentApiService.requestSignature(documentId, request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception(apiResponse.message ?: "Request failed"))
                }
            } else {
                Result.Error(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeDocument(documentId: String): Flow<Document?> {
        return documentDao.observeDocument(documentId).map { it?.let { DocumentMapper.toDomain(it) } }
    }

    override fun observeAllDocuments(): Flow<List<Document>> {
        return documentDao.observeAllDocuments().map { list -> list.map { DocumentMapper.toDomain(it) } }
    }
}
