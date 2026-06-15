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
import com.edham.logistics.ui.home.supervisor.adapter.FleetAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorFleetNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var fleetAdapter: FleetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_fleet_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadFleetData()
        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView(view: View) {
        val rvFleet = view.findViewById<RecyclerView>(R.id.rvFleetList)
        rvFleet.layoutManager = LinearLayoutManager(requireContext())
        fleetAdapter = FleetAdapter(emptyList())
        rvFleet.adapter = fleetAdapter

        view.findViewById<View>(R.id.btn_add_vehicle).setOnClickListener {
            showAddVehicleDialog()
        }
    }

    private fun showAddVehicleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_vehicle, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()

        dialogView.findViewById<View>(R.id.btn_save).setOnClickListener {
            val plate = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_plate).text.toString()
            val type = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_type).text.toString()
            
            if (plate.isNotEmpty() && type.isNotEmpty()) {
                viewModel.addVehicle(com.edham.logistics.core.network.api.VehicleItem(
                    id = "", plateNumber = plate, type = type, 
                    driverName = null, lastMaintenance = null, 
                    temperature = 0.0, mileage = 0.0, status = "READY"
                ))
                dialog.dismiss()
            } else {
                Toast.makeText(context, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun observeViewModel(view: View) {
        viewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            fleetAdapter.updateData(vehicles)
            
            view.findViewById<TextView>(R.id.tv_fleet_summary).text = "${vehicles.size} مركبة مسجلة"
            view.findViewById<TextView>(R.id.tv_ready_vehicles).text = 
                vehicles.count { it.status.uppercase() == "ACTIVE" || it.status.uppercase() == "READY" }.toString()
            view.findViewById<TextView>(R.id.tv_maintenance_vehicles).text = 
                vehicles.count { it.status.uppercase() == "MAINTENANCE" || it.status.uppercase() == "GROUNDED" }.toString()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
