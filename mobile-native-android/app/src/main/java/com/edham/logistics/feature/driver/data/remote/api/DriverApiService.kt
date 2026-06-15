package com.edham.logistics.feature.driver.data.remote.api

import com.edham.logistics.feature.driver.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface DriverApiService {
    
    @GET("drivers/{driverId}/profile")
    suspend fun getDriverProfile(@Path("driverId") driverId: String): Response<DriverProfileDto>
    
    @PUT("drivers/{driverId}/profile")
    suspend fun updateDriverProfile(
        @Path("driverId") driverId: String,
        @Body profile: DriverProfileDto
    ): Response<DriverProfileDto>
    
    @GET("drivers/{driverId}/shipments")
    suspend fun getDriverShipments(
        @Path("driverId") driverId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<ShipmentSummaryDto>>
    
    @PUT("shipments/{shipmentId}/status")
    suspend fun updateShipmentStatus(
        @Path("shipmentId") shipmentId: String,
        @Query("status") status: String
    ): Response<Unit>
    
    @PUT("drivers/{driverId}/location")
    suspend fun updateDriverLocation(
        @Path("driverId") driverId: String,
        @Body location: DriverLocationDto
    ): Response<Unit>
    
    @POST("shipments/{shipmentId}/start")
    suspend fun startTrip(@Path("shipmentId") shipmentId: String): Response<Unit>
    
    @POST("shipments/{shipmentId}/complete")
    suspend fun completeTrip(
        @Path("shipmentId") shipmentId: String,
        @Body photos: List<String>
    ): Response<Unit>
    
    @POST("shipments/{shipmentId}/cancel")
    suspend fun cancelTrip(
        @Path("shipmentId") shipmentId: String,
        @Query("reason") reason: String
    ): Response<Unit>
    
    @POST("shipments/{shipmentId}/report-issue")
    suspend fun reportIssue(
        @Path("shipmentId") shipmentId: String,
        @Body issueData: IssueReportDto
    ): Response<Unit>
    
    // New driver-specific endpoints
    @GET("drivers/{driverId}/dashboard")
    suspend fun getDriverDashboard(@Path("driverId") driverId: String): Response<DriverDashboardDto>
    
    @POST("trips/action")
    suspend fun performTripAction(@Body action: TripActionDto): Response<Unit>
    
    @PUT("trips/{tripId}/status")
    suspend fun updateTripStatus(@Body status: TripUpdateDto): Response<Unit>
    
    @POST("trips/{tripId}/delivery-proof")
    suspend fun uploadDeliveryProof(
        @Path("tripId") tripId: String,
        @Body proof: DeliveryProofDto
    ): Response<Unit>
    
    @GET("trips/{tripId}/route")
    suspend fun getTripRoute(@Path("tripId") tripId: String): Response<RouteDto>
    
    @POST("trips/{tripId}/location")
    suspend fun updateTripLocation(
        @Path("tripId") tripId: String,
        @Body location: LocationDto
    ): Response<Unit>
}
