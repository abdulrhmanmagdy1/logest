package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.feature.driver.data.models.DriverProfile
import com.edham.logistics.ui.home.supervisor.adapter.DriverStatusAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverSelectionBottomSheet(
    private val onDriverSelected: (DriverProfile) -> Unit
) : BottomSheetDialogFragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var adapter: DriverStatusAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_driver_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        observeViewModel()
        
        viewModel.loadDashboardData() // To fetch drivers list
        viewModel.loadFleetData()     // To fetch vehicles for grounding check
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvDrivers)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = DriverStatusAdapter(emptyList()) { driver ->
            onDriverSelected(driver)
            dismiss()
        }
        rv.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.drivers.observe(viewLifecycleOwner) { drivers ->
            val vehicles = viewModel.vehicles.value ?: emptyList()
            
            // Grounding Enforcement: Filter out drivers whose vehicles are in maintenance or grounded
            val availableDrivers = drivers.filter { driver ->
                val vehicle = vehicles.find { it.plateNumber == driver.plateNumber }
                vehicle == null || (
                    vehicle.status.uppercase() != "MAINTENANCE" && 
                    vehicle.status.uppercase() != "GROUNDED" &&
                    vehicle.status.uppercase() != "INACTIVE"
                )
            }
            
            adapter.updateData(availableDrivers)
        }

        viewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            // Re-trigger driver list update when vehicles are loaded
            viewModel.drivers.value?.let { drivers ->
                val availableDrivers = drivers.filter { driver ->
                    val vehicle = vehicles.find { it.plateNumber == driver.plateNumber }
                    vehicle == null || (
                        vehicle.status.uppercase() != "MAINTENANCE" && 
                        vehicle.status.uppercase() != "GROUNDED" &&
                        vehicle.status.uppercase() != "INACTIVE"
                    )
                }
                adapter.updateData(availableDrivers)
            }
        }
    }
}
