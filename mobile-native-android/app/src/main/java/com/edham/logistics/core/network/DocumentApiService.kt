package com.edham.logistics.core.network

import com.edham.logistics.data.remote.dto.DocumentDto
import com.edham.logistics.data.remote.dto.DocumentTemplateDto
import com.edham.logistics.data.remote.dto.request.CreateDocumentRequest
import com.edham.logistics.data.remote.dto.request.ShareDocumentRequest
import com.edham.logistics.data.remote.dto.request.SignDocumentRequest
import com.edham.logistics.data.remote.dto.request.SignatureRequestData
import com.edham.logistics.data.remote.dto.request.UpdateDocumentRequest
import com.edham.logistics.data.remote.dto.response.PdfResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DocumentApiService {
    
    // Create Document
    @POST("documents/create")
    suspend fun createDocument(
        @Body request: CreateDocumentRequest
    ): Response<ApiResponse<DocumentDto>>
    
    // Get Document
    @GET("documents/{id}")
    suspend fun getDocument(
        @Path("id") documentId: String
    ): Response<ApiResponse<DocumentDto>>
    
    // Update Document
    @PUT("documents/{id}")
    suspend fun updateDocument(
        @Path("id") documentId: String,
        @Body request: UpdateDocumentRequest
    ): Response<ApiResponse<DocumentDto>>
    
    // Generate PDF
    @POST("documents/{id}/generate-pdf")
    suspend fun generatePDF(
        @Path("id") documentId: String
    ): Response<ApiResponse<PdfResponse>>
    
    // Sign Document
    @POST("documents/{id}/sign")
    suspend fun signDocument(
        @Path("id") documentId: String,
        @Body request: SignDocumentRequest
    ): Response<ApiResponse<DocumentDto>>
    
    // List Documents
    @GET("documents")
    suspend fun listDocuments(
        @Query("status") status: String?,
        @Query("type") type: String?,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<DocumentDto>>>
    
    // Search Documents
    @GET("documents/search")
    suspend fun searchDocuments(
        @Query("query") query: String,
        @Query("filters") filters: String?
    ): Response<ApiResponse<List<DocumentDto>>>
    
    // Delete Document
    @DELETE("documents/{id}")
    suspend fun deleteDocument(
        @Path("id") documentId: String
    ): Response<ApiResponse<Unit>>
    
    // Get Templates
    @GET("documents/templates")
    suspend fun getTemplates(
        @Query("type") type: String?
    ): Response<ApiResponse<List<DocumentTemplateDto>>>
    
    // Upload Attachment
    @Multipart
    @POST("documents/{id}/upload-attachment")
    suspend fun uploadAttachment(
        @Path("id") documentId: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<DocumentDto>>
    
    // Share Document
    @POST("documents/{id}/share")
    suspend fun shareDocument(
        @Path("id") documentId: String,
        @Body request: ShareDocumentRequest
    ): Response<ApiResponse<Unit>>
    
    // Request Signature
    @POST("documents/{id}/request-signature")
    suspend fun requestSignature(
        @Path("id") documentId: String,
        @Body request: SignatureRequestData
    ): Response<ApiResponse<Unit>>
    
    // Download Document
    @GET("documents/{id}/download")
    suspend fun downloadDocument(
        @Path("id") documentId: String
    ): ResponseBody
}
