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
    @GET("drivers/{id}/profile")
    suspend fun getProfile(
        @Path("id") driverId: String
    ): Response<ApiResponse<DriverProfile>>

    @GET("trips/{id}/path")
    suspend fun getTripPath(
        @Path("id") tripId: String
    ): Response<ApiResponse<List<Waypoint>>>

    @POST("trips/{id}/accept")
    suspend fun acceptTrip(
        @Path("id") tripId: String
    ): Response<Unit>

    @POST("trips/{id}/reject")
    suspend fun rejectTrip(
        @Path("id") tripId: String,
        @Query("reason") reason: String
    ): Response<Unit>

    @POST("drivers/{id}/expenses")
    suspend fun submitExpense(
        @Path("id") driverId: String,
        @Query("tripId") tripId: String?,
        @Query("amount") amount: Double,
        @Query("type") type: String,
        @Query("description") description: String,
        @Query("imageUrl") imageUrl: String?
    ): Response<Unit>

    @PUT("trips/{id}/status")
    suspend fun updateTripStatus(
        @Path("id") tripId: String,
        @Query("status") status: String,
        @Query("notes") notes: String? = null
    ): Response<Unit>

    @POST("drivers/{id}/telemetry")
    suspend fun reportTelemetry(
        @Path("id") driverId: String,
        @Query("battery") battery: Int,
        @Query("signal") signal: Int,
        @Query("temperature") temperature: Double? = null
    ): Response<Unit>
}
