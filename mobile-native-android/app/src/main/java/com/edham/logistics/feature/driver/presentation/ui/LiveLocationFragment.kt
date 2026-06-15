package com.edham.logistics.feature.driver.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.edham.logistics.databinding.FragmentLiveLocationBinding
import com.edham.logistics.feature.driver.presentation.viewmodels.LiveLocationViewModel
import com.edham.logistics.feature.driver.service.LocationForegroundService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LiveLocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLiveLocationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LiveLocationViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.locationUpdate.collect { update ->
                update?.let {
                    binding.tvSpeed.text = it.speed.toInt().toString()
                    binding.tvHeading.text = "${it.heading.toInt()}°"
                    binding.tvAccuracyValue.text = "${it.accuracy.toInt()}m"
                    binding.pbGpsSignal.progress = calculateSignalStrength(it.accuracy)
                    
                    val latLng = LatLng(it.lat, it.lng)
                    googleMap?.clear()
                    googleMap?.addMarker(MarkerOptions().position(latLng))
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lastUpdatedSeconds.collect { seconds ->
                binding.tvLastUpdated.text = getString(R.string.last_updated, seconds)
            }
        }
    }

    private fun setupListeners() {
        binding.btnPauseResume.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Resume
                context?.startService(Intent(context, LocationForegroundService::class.java))
            } else {
                // Pause
                context?.stopService(Intent(context, LocationForegroundService::class.java))
            }
        }
    }

    private fun calculateSignalStrength(accuracy: Float): Int {
        return when {
            accuracy < 10 -> 100
            accuracy < 20 -> 80
            accuracy < 50 -> 60
            accuracy < 100 -> 40
            else -> 20
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
