package com.edham.logistics.feature.admin.data.remote

import com.edham.logistics.core.network.UnifiedResponseDto
import com.edham.logistics.feature.admin.domain.model.AdminDashboardData
import retrofit2.http.*

interface AdminApi {

    @GET("api/v1/admin/dashboard/analytics")
    suspend fun getDashboardAnalytics(
        @Query("filter") filter: String = "WEEKLY",
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): UnifiedResponseDto<AdminDashboardData>

    @GET("api/v1/supervisor/stats")
    suspend fun getSupervisorStats(): UnifiedResponseDto<Map<String, Any>>

    @GET("api/v1/supervisor/drivers")
    suspend fun getDrivers(): UnifiedResponseDto<List<Map<String, Any>>>

    @GET("api/v1/supervisor/invoices")
    suspend fun getInvoices(): UnifiedResponseDto<List<Map<String, Any>>>

    @GET("api/v1/supervisor/maintenance")
    suspend fun getMaintenanceRecords(): UnifiedResponseDto<List<Map<String, Any>>>

    @POST("api/v1/supervisor/assign-trip")
    suspend fun assignTrip(
        @Query("tripId") tripId: String,
        @Query("driverId") driverId: String
    ): UnifiedResponseDto<Unit>
}
