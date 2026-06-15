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
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvDrivers)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = DriverStatusAdapter(emptyList()) // We'll update the adapter to handle clicks
        rv.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.drivers.observe(viewLifecycleOwner) { drivers ->
            adapter.updateData(drivers)
        }
    }
}
