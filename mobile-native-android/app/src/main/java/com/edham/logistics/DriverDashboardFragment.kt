package com.edham.logistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.databinding.FragmentDriverDashboardBinding
import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.presentation.adapter.TripAdapter
import com.edham.logistics.feature.driver.presentation.viewmodels.DriverDashboardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DriverDashboardFragment : Fragment() {

    private var _binding: FragmentDriverDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DriverDashboardViewModel by viewModels()
    private lateinit var tripAdapter: TripAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripAdapter = TripAdapter(emptyList()) { trip ->
            // Use safe args if available, otherwise manual bundle
            val bundle = Bundle().apply { putString("tripId", trip.id) }
            findNavController().navigate(R.id.action_driverDashboard_to_activeTrip, bundle)
        }
        
        binding.rvRecentTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripAdapter
        }
        
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.profile.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                val profile = resource.data
                binding.tvDriverName.text = "${profile?.firstName} ${profile?.lastName}"
                binding.tvPlateNumber.text = profile?.plateNumber ?: "---"
                binding.tvStatus.text = profile?.status ?: "---"
            }
        }

        viewModel.newAssignment.observe(viewLifecycleOwner) { trip ->
            trip?.let {
                showNewAssignmentDialog(it)
            }
        }

        viewModel.statsResource.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is com.edham.logistics.core.utils.Resource.Success -> {
                    val stats = resource.data
                    binding.tvTodayDistance.text = stats?.todayDistance?.toInt().toString()
                    binding.tvTodayEarnings.text = "ريال ${stats?.todayEarnings?.toInt()}"
                    binding.tvRating.text = stats?.rating.toString()
                    binding.tvFuelLevel.text = "${stats?.fuelLevel}%"
                    binding.swipeRefresh.isRefreshing = false
                }
                is Resource.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                }
            }
        }

        viewModel.trips.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                tripAdapter.updateTrips(resource.data ?: emptyList())
                binding.tvTripsCount.text = getString(R.string.active_trips_count, resource.data?.size ?: 0)
            }
        }
    }

    private fun showNewAssignmentDialog(trip: Trip) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("رحلة جديدة من المشرف")
            .setMessage("لديك تعيين جديد: من ${trip.origin} إلى ${trip.destination}\nالأرباح: ${trip.earnings} ريال")
            .setPositiveButton("قبول") { _, _ ->
                viewModel.acceptAssignment(trip.id)
            }
            .setNegativeButton("رفض") { _, _ ->
                viewModel.rejectAssignment(trip.id, "Busy")
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
