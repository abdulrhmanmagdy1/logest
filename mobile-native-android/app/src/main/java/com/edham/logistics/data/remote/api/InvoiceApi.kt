package com.edham.logistics.data.remote.api

import com.edham.logistics.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface InvoiceApi {

    @GET("invoices")
    suspend fun getAllInvoices(): Response<List<InvoiceDto>>

    @GET("invoices/{id}")
    suspend fun getInvoiceById(@Path("id") id: String): Response<InvoiceDto>

    @POST("invoices")
    suspend fun createInvoice(@Body request: CreateInvoiceRequest): Response<InvoiceDto>

    @PUT("invoices/{id}/status")
    suspend fun updateInvoiceStatus(
        @Path("id") id: String,
        @Query("status") status: String
    ): Response<InvoiceDto>

    @DELETE("invoices/{id}")
    suspend fun deleteInvoice(@Path("id") id: String): Response<Unit>
}
