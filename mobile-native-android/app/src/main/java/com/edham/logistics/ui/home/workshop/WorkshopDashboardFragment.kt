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
import com.edham.logistics.ui.home.supervisor.adapter.MaintenanceAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopDashboardFragment : Fragment() {

    private val viewModel: WorkshopViewModel by viewModels()
    private lateinit var maintenanceAdapter: MaintenanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workshop_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadWorkshopDashboard()
    }

    private fun setupRecyclerView(view: View) {
        val rvJobs = view.findViewById<RecyclerView>(R.id.rv_current_jobs)
        rvJobs.layoutManager = LinearLayoutManager(requireContext())
        maintenanceAdapter = MaintenanceAdapter(emptyList())
        rvJobs.adapter = maintenanceAdapter
    }

    private fun observeViewModel(view: View) {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.tv_urgent_repairs).text = stats.maintenance_alerts.toString()
        }

        viewModel.currentJobs.observe(viewLifecycleOwner) { jobs ->
            maintenanceAdapter.updateData(jobs)
            view.findViewById<TextView>(R.id.tv_vehicles_in_service).text = 
                jobs.count { it.status == "IN_PROGRESS" }.toString()
            view.findViewById<TextView>(R.id.tv_completed_today).text = 
                jobs.count { it.status == "COMPLETED" }.toString()
        }

        viewModel.partsStock.observe(viewLifecycleOwner) { parts ->
            view.findViewById<TextView>(R.id.tv_parts_stock).text = parts.size.toString()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
