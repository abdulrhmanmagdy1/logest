package com.edham.logistics.core.network.api

import com.edham.logistics.Invoice
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Invoice & billing endpoints.
 */
interface InvoiceApi {

    @GET("invoices")
    suspend fun getInvoices(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PagedResponse<Invoice>>

    @GET("invoices/{id}")
    suspend fun getInvoice(@Path("id") id: String): Response<Invoice>

    @POST("invoices/{id}/pay")
    suspend fun payInvoice(@Path("id") id: String): Response<Invoice>
}
