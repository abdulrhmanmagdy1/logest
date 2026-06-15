package com.edham.logistics.ui.home.workshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.workshop.adapter.MaintenanceTask
import com.edham.logistics.ui.home.workshop.adapter.MaintenanceTaskAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopDashboardFragment : Fragment() {

    private val viewModel: WorkshopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workshop_dashboard_elite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvWorkshopTasks)
        rv.layoutManager = LinearLayoutManager(requireContext())
        
        observeViewModel(view)
        setupListeners(view)

        viewModel.loadDashboardData()
        viewModel.loadMaintenanceTasks()
        startHealthMonitoring()
    }

    private fun startHealthMonitoring() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                viewModel.refreshHealth()
                handler.postDelayed(this, 5000)
            }
        })
    }

    private fun observeViewModel(view: View) {
        viewModel.oilChangeAlerts.observe(viewLifecycleOwner) { alerts ->
            if (alerts.isNotEmpty()) {
                Toast.makeText(context, "⚠️ يوجد ${alerts.size} تنبيهات لتغيير الزيت!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.tvFleetEfficiency).text = "${stats.fleetHealthScore}%"
            view.findViewById<TextView>(R.id.tvGroundedCount).text = stats.groundedTrucks.toString()
            view.findViewById<TextView>(R.id.tvLowStockCount).text = stats.lowStockItems.toString()
        }

        viewModel.systemLatency.observe(viewLifecycleOwner) { ms ->
            val dot = view.findViewById<View>(R.id.heartbeatDot)
            val label = view.findViewById<TextView>(R.id.tvLatency)
            if (dot != null && label != null) {
                label.text = "${ms}ms"
                dot.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    if (ms < 300) requireContext().getColor(R.color.status_success) 
                    else requireContext().getColor(R.color.status_error)
                )
            }
        }

        viewModel.records.observe(viewLifecycleOwner) { records ->
            val tasks = records.map { 
                MaintenanceTask(it.vehicleId, it.serviceType, it.status, it.date)
            }
            view.findViewById<RecyclerView>(R.id.rvWorkshopTasks).adapter = MaintenanceTaskAdapter(tasks)
        }
    }

    private fun setupListeners(view: View) {
        // No btnGroundVehicle in current dashboard elite layout, 
        // it's likely in the list or sidebar
    }

    private fun showGroundDialog() {
        val etReason = android.widget.EditText(requireContext())
        etReason.hint = "سبب التأريض"
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("تأريض مركبة (إيقاف)")
            .setView(etReason)
            .setPositiveButton("إيقاف فوراً") { _, _ ->
                viewModel.groundVehicle("T-01", etReason.text.toString())
                Toast.makeText(context, "تم إيقاف الشاحنة وإبلاغ المشرف 🚫", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
}
