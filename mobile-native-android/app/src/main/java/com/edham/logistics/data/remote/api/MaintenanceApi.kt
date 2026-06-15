package com.edham.logistics.data.remote.api

import com.edham.logistics.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MaintenanceApi {

    @GET("maintenance")
    suspend fun getAllMaintenanceRecords(): Response<List<MaintenanceRecordDto>>

    @GET("maintenance/vehicle/{vehicleId}")
    suspend fun getVehicleMaintenanceHistory(
        @Path("vehicleId") vehicleId: String
    ): Response<List<MaintenanceRecordDto>>

    @POST("maintenance")
    suspend fun createMaintenanceRecord(
        @Body request: CreateMaintenanceRequest
    ): Response<MaintenanceRecordDto>

    @GET("maintenance/alerts")
    suspend fun getMaintenanceAlerts(): Response<List<MaintenanceAlertDto>>

    @PUT("maintenance/alerts/{id}/dismiss")
    suspend fun dismissMaintenanceAlert(@Path("id") id: String): Response<Unit>

    @GET("maintenance/oil-changes")
    suspend fun getOilChanges(): Response<List<OilChangeDto>>

    @POST("maintenance/oil-change")
    suspend fun recordOilChange(
        @Body request: Map<String, Any>
    ): Response<OilChangeDto>
}
