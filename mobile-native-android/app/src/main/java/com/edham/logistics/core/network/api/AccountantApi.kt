package com.edham.logistics.core.network.api

import retrofit2.Response
import retrofit2.http.*

interface AccountantApi {

    @GET("accountant/dashboard")
    suspend fun getDashboardStats(): Response<ApiResponse<AccountantDashboardStats>>

    @GET("accountant/settlements")
    suspend fun getDriverSettlements(): Response<ApiResponse<List<DriverSettlement>>>

    @POST("accountant/settlements/{id}/approve")
    suspend fun approveExpense(@Path("id") expenseId: String): Response<Unit>

    @POST("accountant/settlements/{id}/reject")
    suspend fun rejectExpense(@Path("id") expenseId: String, @Query("reason") reason: String): Response<Unit>

    @GET("accountant/debts")
    suspend fun getDebtAgingReport(): Response<ApiResponse<DebtAgingReport>>

    @POST("accountant/debts/{id}/collect")
    suspend fun collectPayment(
        @Path("id") clientId: String,
        @Query("amount") amount: Double,
        @Query("method") method: String,
        @Query("notes") notes: String? = null
    ): Response<Unit>

    @GET("accountant/workshop-requests")
    suspend fun getWorkshopRequests(): Response<ApiResponse<List<WorkshopFinancialRequest>>>

    @POST("accountant/workshop-requests/{id}/approve")
    suspend fun approveWorkshopRequest(@Path("id") requestId: String): Response<Unit>

    @POST("accountant/receipt-vouchers")
    suspend fun submitReceiptVoucher(
        @Query("clientName") clientName: String,
        @Query("amount") amount: Double,
        @Query("method") method: String
    ): Response<Unit>

    @GET("accountant/client/{id}/statement")
    suspend fun getStatementOfAccount(@Path("id") clientId: String): Response<ApiResponse<StatementOfAccount>>
}

data class AccountantDashboardStats(
    val liquidity: Double,
    val outstanding_debts: Double,
    val monthly_expenses: Double,
    val net_profit: Double,
    val revenue_history: List<ChartData>,
    val expense_distribution: List<ChartData>,
    val profit_history: List<ChartData>
)

data class DriverSettlement(
    val driverId: String,
    val driverName: String,
    val advanceAmount: Double,
    val approvedExpenses: Double,
    val lastUpdate: String,
    val expenses: List<DriverExpense>
)

data class DriverExpense(
    val id: String,
    val type: String, // FUEL, TOLL, OTHER
    val amount: Double,
    val description: String,
    val imageUrl: String?,
    val status: String, // PENDING, APPROVED, REJECTED
    val date: String
)

data class DebtAgingReport(
    val totalOutstanding: Double,
    val t30_days: Double,
    val t60_days: Double,
    val t90_plus_days: Double,
    val clients: List<ClientDebt>
)

data class ClientDebt(
    val clientId: String,
    val clientName: String,
    val amount: Double,
    val delayDays: Int,
    val status: String // LATE, CRITICAL, OK
)

data class WorkshopFinancialRequest(
    val id: String,
    val title: String,
    val truckId: String,
    val truckType: String,
    val amount: Double,
    val priority: String,
    val requestedBy: String,
    val details: String,
    val date: String
)

data class StatementOfAccount(
    val clientId: String,
    val clientName: String,
    val totalInvoiced: Double,
    val totalPaid: Double,
    val remaining: Double,
    val entries: List<SoAEntry>
)

data class SoAEntry(
    val id: String,
    val type: String, // SHIPMENT, PAYMENT
    val date: String,
    val amount: Double,
    val paid: Double,
    val status: String
)
