package com.edham.logistics.core.network.api

import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.data.models.DriverProfile
import retrofit2.Response
import retrofit2.http.*

interface SupervisorApi {

    @GET("supervisor/stats")
    suspend fun getStats(): Response<ApiResponse<SupervisorStats>>

    @GET("customer/stats")
    suspend fun getCustomerStats(): Response<ApiResponse<CustomerStats>>

    @GET("supervisor/orders")
    suspend fun getOrders(
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<Trip>>>

    @GET("supervisor/drivers")
    suspend fun getDrivers(): Response<ApiResponse<List<DriverProfile>>>

    @POST("supervisor/assign-trip")
    suspend fun assignTrip(
        @Query("tripId") tripId: String,
        @Query("driverId") driverId: String
    ): Response<Unit>

    @POST("supervisor/update-price")
    suspend fun updateShipmentPrice(
        @Query("shipmentId") shipmentId: String,
        @Query("newPrice") price: Double,
        @Query("notes") notes: String? = null
    ): Response<Unit>

    @GET("supervisor/shipment/{id}/audit-log")
    suspend fun getShipmentAuditLog(@Path("id") shipmentId: String): Response<ApiResponse<List<AuditEntry>>>

    @GET("supervisor/driver/{driverId}/active-shipment")
    suspend fun getActiveShipmentByDriver(@Path("driverId") driverId: Long): Response<ApiResponse<com.edham.logistics.feature.driver.data.models.Trip>>

    @POST("customer/create-trip")
    suspend fun createTrip(@Body trip: Trip): Response<ApiResponse<Trip>>

    @GET("supervisor/invoices")
    suspend fun getInvoices(): Response<ApiResponse<List<Invoice>>>

    @GET("supervisor/maintenance")
    suspend fun getMaintenanceRecords(): Response<ApiResponse<List<MaintenanceRecord>>>

    @GET("supervisor/parts")
    suspend fun getPartsInventory(): Response<ApiResponse<List<PartItem>>>

    @GET("supervisor/reports/revenue")
    suspend fun getRevenueReport(): Response<ApiResponse<List<ChartData>>>

    @GET("supervisor/reports/expenses")
    suspend fun getExpensesReport(): Response<ApiResponse<List<ChartData>>>

    @GET("supervisor/vehicles")
    suspend fun getVehicles(): Response<ApiResponse<List<VehicleItem>>>

    @GET("surveys")
    suspend fun getAllSurveys(): Response<ApiResponse<List<com.edham.logistics.feature.driver.data.models.SurveySubmission>>>

    @POST("supervisor/vehicles")
    suspend fun addVehicle(@Body vehicle: VehicleItem): Response<ApiResponse<VehicleItem>>

    @GET("tracking/drivers/locations")
    suspend fun getAllDriverLocations(): Response<ApiResponse<List<Map<String, Any>>>>

    @GET("supervisor/alerts")
    suspend fun getSmartAlerts(): Response<ApiResponse<List<SmartAlert>>>

    @GET("accountant/dashboard")
    suspend fun getAccountantStats(): Response<ApiResponse<AccountantStats>>
}

data class AccountantStats(
    val net_profit: Double,
    val total_revenue: Double,
    val total_expenses: Double,
    val outstanding_invoices: Double,
    val urgent_overdue: Double
)

data class CustomerStats(
    val pending: Int,
    val active: Int,
    val completed: Int,
    val wallet_balance: Double = 0.0
)

data class SupervisorStats(
    val delivered_today: Int,
    val in_transit: Int,
    val available_vehicles: Int,
    val total_earnings: Double,
    val pending_invoices_count: Int,
    val maintenance_alerts: Int
)

data class Invoice(
    val id: String,
    val clientName: String,
    val amount: Double,
    val date: String,
    val status: String
)

data class MaintenanceRecord(
    val vehicleId: String,
    val serviceType: String,
    val date: String,
    val cost: Double,
    val status: String
)

data class PartItem(
    val code: String,
    val name: String,
    val quantity: Int,
    val minQuantity: Int,
    val status: String
)

data class ChartData(
    val label: String,
    val value: Double
)

data class VehicleItem(
    val id: String,
    val type: String,
    val plateNumber: String,
    val driverName: String?,
    val lastMaintenance: String?,
    val temperature: Double,
    val mileage: Double,
    val status: String
)

data class AuditEntry(
    val id: String,
    val action: String,
    val user: String,
    val timestamp: String,
    val details: String? = null
)
