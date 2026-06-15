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
import com.edham.logistics.ui.home.supervisor.adapter.DriverStatusAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorDriversNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var driverAdapter: DriverStatusAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_drivers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        observeViewModel(view)
        
        viewModel.loadDashboardData() // This loads drivers list
    }

    private fun setupRecyclerView(view: View) {
        val rvDrivers = view.findViewById<RecyclerView>(R.id.rvDrivers)
        rvDrivers.layoutManager = LinearLayoutManager(requireContext())
        driverAdapter = DriverStatusAdapter(emptyList()) { driver ->
            val intent = android.content.Intent(requireContext(), SupervisorDriverProfileActivity::class.java)
            intent.putExtra("DRIVER_ID", driver.id)
            startActivity(intent)
        }
        rvDrivers.adapter = driverAdapter
    }

    private fun observeViewModel(view: View) {
        viewModel.drivers.observe(viewLifecycleOwner) { drivers ->
            driverAdapter.updateData(drivers)
            view.findViewById<TextView>(R.id.tv_drivers_summary)?.text = "${drivers.size} سائق مسجل"
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
