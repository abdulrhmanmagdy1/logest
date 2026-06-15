package com.edham.logistics.ui.home.supervisor

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
import com.edham.logistics.ui.home.supervisor.adapter.MaintenanceAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorMaintenanceNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var maintenanceAdapter: MaintenanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_maintenance_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadMaintenanceData()
        viewModel.loadDashboardData() // For readiness stats
    }

    private fun setupRecyclerView(view: View) {
        val rvHistory = view.findViewById<RecyclerView>(R.id.rvMaintenanceHistory)
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        maintenanceAdapter = MaintenanceAdapter(emptyList())
        rvHistory.adapter = maintenanceAdapter
    }

    private fun observeViewModel(view: View) {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.tv_oil_alerts).text = stats.maintenance_alerts.toString()
            // Placeholder for fleet readiness if not in stats yet
            view.findViewById<TextView>(R.id.tv_fleet_readiness).text = "95%"
        }

        viewModel.maintenanceRecords.observe(viewLifecycleOwner) { records ->
            maintenanceAdapter.updateData(records)
            view.findViewById<TextView>(R.id.tv_maintenance_summary).text = 
                "${records.filter { it.status == "IN_PROGRESS" }.size} مركبات في الصيانة حالياً"
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
