package com.edham.logistics.feature.driver.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edham.logistics.R
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.databinding.FragmentActiveTripBinding
import com.edham.logistics.feature.driver.presentation.adapter.StatusEvent
import com.edham.logistics.feature.driver.presentation.adapter.StatusTimelineAdapter
import com.edham.logistics.feature.driver.presentation.viewmodels.ActiveTripViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActiveTripFragment : Fragment() {

    private var _binding: FragmentActiveTripBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActiveTripViewModel by viewModels()
    private lateinit var timelineAdapter: StatusTimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timelineAdapter = StatusTimelineAdapter(emptyList())
        binding.rvStatusTimeline.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timelineAdapter
        }

        binding.btnOpenNavigation.setOnClickListener {
            val trip = (viewModel.trip.value as? Resource.Success)?.data
            trip?.let {
                val gmmIntentUri = Uri.parse("google.navigation:q=${it.destLat},${it.destLng}&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        arguments?.getString("tripId")?.let { viewModel.loadTrip(it) }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.trip.collect { resource ->
                if (resource is Resource.Success) {
                    val trip = resource.data
                    binding.tvTripId.text = trip?.tripId
                    binding.tvDistance.text = "${trip?.distance} كم"
                    
                    timelineAdapter.updateEvents(listOf(
                        StatusEvent("وصل موقع الاستلام", "10:00", true),
                        StatusEvent("تم استلام البضاعة", "10:05", true),
                        StatusEvent("في الطريق للتسليم", "الآن", true),
                        StatusEvent("التسليم للعميل", "~10:28", false, isLast = true)
                    ))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eta.collect { eta ->
                binding.tvEtaBadge.text = "🕒 وصول $eta"
                binding.tvRemainingInfo.text = "في الطريق • $eta متبقية"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pathAnalysis.collect { analysis ->
                analysis?.let {
                    binding.tvPathAnalysis.text = "${it.optimizationNote}\nالكفاءة: ${it.efficiency}% | الوقود المتوقع: ${it.estimatedFuel}%"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
