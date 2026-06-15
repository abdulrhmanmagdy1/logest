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
import com.edham.logistics.ui.home.supervisor.adapter.LoadsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorLoadsNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var loadsAdapter: LoadsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_loads_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadActiveTrips()
        viewModel.loadDashboardData() // To get drivers list
    }

    private fun setupRecyclerView(view: View) {
        val rvLoads = view.findViewById<RecyclerView>(R.id.rvLoadsList)
        rvLoads.layoutManager = LinearLayoutManager(requireContext())
        loadsAdapter = LoadsAdapter(emptyList()) { trip ->
            showDriverSelectionDialog(trip)
        }
        rvLoads.adapter = loadsAdapter
    }

    private fun showDriverSelectionDialog(trip: com.edham.logistics.feature.driver.data.models.Trip) {
        val bottomSheet = DriverSelectionBottomSheet { driver ->
            viewModel.assignDriver(trip.id, driver.id)
            Toast.makeText(context, "تم إرسال المهمة للسائق ${driver.firstName}", Toast.LENGTH_SHORT).show()
        }
        bottomSheet.show(childFragmentManager, "DriverSelection")
    }

    private fun observeViewModel(view: View) {
        viewModel.trips.observe(viewLifecycleOwner) { trips ->
            loadsAdapter.updateData(trips)
            view.findViewById<TextView>(R.id.tv_loads_summary).text = "${trips.size} حمولة نشطة"
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
