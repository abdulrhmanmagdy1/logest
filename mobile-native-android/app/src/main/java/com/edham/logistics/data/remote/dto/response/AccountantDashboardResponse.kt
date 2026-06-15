package com.edham.logistics.data.remote.dto.response

import com.edham.logistics.data.remote.dto.InvoiceDto
import com.google.gson.annotations.SerializedName

data class AccountantDashboardResponse(
    val success: Boolean,
    val data: AccountantDashboardData
)

data class AccountantDashboardData(
    @SerializedName("todayRevenue") val todayRevenue: Double,
    @SerializedName("pendingInvoices") val pendingInvoices: Int,
    @SerializedName("totalDebts") val totalDebts: Int,
    @SerializedName("debtsAmount") val debtsAmount: Double,
    @SerializedName("pendingSettlements") val pendingSettlements: Int,
    @SerializedName("recentTransactions") val recentTransactions: List<InvoiceDto>
)
