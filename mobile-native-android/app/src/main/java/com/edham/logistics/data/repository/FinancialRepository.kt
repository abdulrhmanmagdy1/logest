package com.edham.logistics.data.repository

import com.edham.logistics.core.network.api.AccountantApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialRepository @Inject constructor(
    private val api: AccountantApi
) {
    suspend fun getDashboardStats() = api.getDashboardStats()
    suspend fun getDriverSettlements() = api.getDriverSettlements()
    suspend fun approveExpense(id: String) = api.approveExpense(id)
    suspend fun rejectExpense(id: String, reason: String) = api.rejectExpense(id, reason)
    suspend fun getDebtAgingReport() = api.getDebtAgingReport()
    suspend fun getWorkshopRequests() = api.getWorkshopRequests()
    suspend fun approveWorkshopRequest(id: String) = api.approveWorkshopRequest(id)
    suspend fun getStatementOfAccount(clientId: String) = api.getStatementOfAccount(clientId)
}
