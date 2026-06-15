package com.edham.logistics.core.network.api

import com.edham.logistics.feature.driver.data.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface DriverApi {

    @PUT("drivers/{id}/location")
    suspend fun updateLocation(
        @Path("id") driverId: String,
        @Body location: LocationUpdate
    ): Response<Unit>

    @GET("lookup/cargo-types")
    suspend fun getCargoTypes(): Response<List<String>>

    @POST("trips/{id}/waypoints")
    suspend fun addWaypoint(
        @Path("id") tripId: String,
        @Body waypoint: Waypoint
    ): Response<Unit>

    @GET("drivers/{id}/trips")
    suspend fun getTrips(
        @Path("id") driverId: String,
        @Query("date") date: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<Trip>>>

    @GET("drivers/{id}/stats")
    suspend fun getDriverStats(
        @Path("id") driverId: String,
        @Query("date") date: String? = null
    ): Response<ApiResponse<DriverStats>>

    @GET("surveys/post-mission")
    suspend fun getPostMissionSurvey(): Response<ApiResponse<Survey>>

    @POST("surveys/submit")
    suspend fun submitSurvey(
        @Body submission: SurveySubmission
    ): Response<Unit>

    @Multipart
    @POST("shipments/{id}/attachments")
    suspend fun uploadAttachment(
        @Path("id") shipmentId: String,
        @Part file: MultipartBody.Part
    ): Response<Attachment>

    @DELETE("attachments/{file_id}")
    suspend fun deleteAttachment(
        @Path("file_id") fileId: String
    ): Response<Unit>

    @POST("shipments/{id}/delivery-proof")
    suspend fun submitDeliveryProof(
        @Path("id") shipmentId: String,
        @Body proof: DeliveryProof
    ): Response<Unit>
}
