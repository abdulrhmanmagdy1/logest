package com.edham.logistics.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edham.logistics.MockData

/**
 * Shared dashboard ViewModel exposing headline KPIs.
 * Works for Supervisor, Accountant, and Customer dashboards.
 */
class DashboardViewModel : ViewModel() {

    data class KpiSummary(
        val activeLoads: Int,
        val inTransit: Int,
        val deliveredToday: Int,
        val pendingInvoices: Int,
        val availableVehicles: Int,
        val maintenanceAlerts: Int
    )

    private val _kpis = MutableLiveData<KpiSummary>()
    val kpis: LiveData<KpiSummary> = _kpis

    private val _recentActivity = MutableLiveData<List<String>>(emptyList())
    val recentActivity: LiveData<List<String>> = _recentActivity

    fun refresh() {
        val loads = MockData.loads
        val invoices = MockData.invoices
        val vehicles = MockData.vehicles
        val maintenance = MockData.maintenanceRecords

        _kpis.value = KpiSummary(
            activeLoads = loads.count { it.status.equals("In Transit", ignoreCase = true) || it.status.equals("Loading", ignoreCase = true) },
            inTransit = loads.count { it.status.equals("In Transit", ignoreCase = true) },
            deliveredToday = loads.count { it.status.equals("Delivered", ignoreCase = true) },
            pendingInvoices = invoices.count { it.status.equals("Pending", ignoreCase = true) || it.status.equals("Overdue", ignoreCase = true) },
            availableVehicles = vehicles.count { it.status.equals("Available", ignoreCase = true) },
            maintenanceAlerts = maintenance.count { it.status.equals("Pending", ignoreCase = true) || it.status.equals("Scheduled", ignoreCase = true) }
        )

        _recentActivity.value = MockData.notifications.map { "${it.type}: ${it.title}" }
    }
}
