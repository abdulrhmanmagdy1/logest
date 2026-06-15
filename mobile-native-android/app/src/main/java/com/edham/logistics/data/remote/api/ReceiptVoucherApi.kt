package com.edham.logistics.data.remote.api

import com.edham.logistics.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ReceiptVoucherApi {

    @GET("receipts")
    suspend fun getAllReceipts(): Response<List<ReceiptVoucherDto>>

    @GET("receipts/{id}")
    suspend fun getReceiptById(@Path("id") id: String): Response<ReceiptVoucherDto>

    @POST("receipts")
    suspend fun createReceipt(
        @Body request: CreateReceiptVoucherRequest
    ): Response<ReceiptVoucherDto>

    @PUT("receipts/{id}/status")
    suspend fun updateReceiptStatus(
        @Path("id") id: String,
        @Query("status") status: String
    ): Response<ReceiptVoucherDto>

    @DELETE("receipts/{id}")
    suspend fun deleteReceipt(@Path("id") id: String): Response<Unit>

    @GET("receipts/stats")
    suspend fun getReceiptStats(): Response<ReceiptVoucherStatsResponse>
}
