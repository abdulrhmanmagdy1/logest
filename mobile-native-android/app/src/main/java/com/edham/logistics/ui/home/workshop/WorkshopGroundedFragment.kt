package com.edham.logistics.ui.home.workshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.workshop.adapter.GroundedTruckAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopGroundedFragment : Fragment() {

    private val viewModel: WorkshopViewModel by viewModels()
    private lateinit var adapter: GroundedTruckAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workshop_grounding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvGroundedTrucks)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = GroundedTruckAdapter(emptyList()) { vehicle ->
            viewModel.releaseVehicle(vehicle.id)
            Toast.makeText(context, "إعادة ${vehicle.plateNumber} للخدمة... ✅", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter

        observeViewModel()
        viewModel.loadFleetData()
    }

    private fun observeViewModel() {
        viewModel.vehicles.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list.filter { it.status.uppercase() == "GROUNDED" || it.status.uppercase() == "MAINTENANCE" })
        }
    }
}
