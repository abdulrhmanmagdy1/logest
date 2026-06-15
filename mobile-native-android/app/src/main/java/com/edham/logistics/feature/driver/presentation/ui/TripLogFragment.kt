package com.edham.logistics.feature.driver.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.databinding.FragmentTripLogBinding
import com.edham.logistics.feature.driver.presentation.viewmodels.TripLogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TripLogFragment : Fragment() {

    private var _binding: FragmentTripLogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripLogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTrips.layoutManager = LinearLayoutManager(requireContext())
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadTrips() }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cargoTypes.collect { types ->
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCargoType.adapter = adapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.trips.collect { resource ->
                binding.swipeRefresh.isRefreshing = resource is Resource.Loading
                if (resource is Resource.Success) {
                    // Update list
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalEarnings.collect { earnings ->
                binding.tvTotalEarnings.text = "$earnings ريال"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalDistance.collect { distance ->
                binding.tvTotalDistance.text = "$distance كم"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
