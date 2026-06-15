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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.edham.logistics.feature.tracking.data.service.TrackingWebSocketService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SupervisorTrackingNewFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var webSocketService: TrackingWebSocketService

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var driverAdapter: DriverStatusAdapter
    private var googleMap: GoogleMap? = null
    private val markers = mutableMapOf<Long, Marker>()
    private var timer: Timer? = null

    private var tvActiveDriversCount: TextView? = null
    private var btnRefreshMap: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_tracking_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tvActiveDriversCount = view.findViewById(R.id.tvActiveDriversCount)
        btnRefreshMap = view.findViewById(R.id.btnRefreshMap)

        btnRefreshMap?.setOnClickListener {
            viewModel.loadLocations()
            Toast.makeText(context, "تحديث مواقع السائقين...", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupRecyclerView(view)
        observeViewModel()
        
        connectWebSocket()
        startRealTimeTracking()
    }

    private fun connectWebSocket() {
        val token = com.edham.logistics.app.AuthSession.get(requireContext()).token ?: return
        webSocketService.connect(token)
        
        lifecycleScope.launchWhenStarted {
            webSocketService.messageFlow.collectLatest { message ->
                if (message.type == com.edham.logistics.feature.tracking.domain.model.TrackingMessageType.LOCATION_UPDATE) {
                    viewModel.loadLocations()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        
        // Dark Mode Map Style
        try {
            googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark)
            )
        } catch (e: Exception) {}

        googleMap?.setOnMarkerClickListener { marker ->
            val driverId = marker.tag as? Long
            if (driverId != null) {
                showTruckInfoBottomSheet(driverId)
            }
            false
        }

        val saudi = LatLng(23.8859, 45.0792)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(saudi, 5f))
    }

    private fun showTruckInfoBottomSheet(driverId: Long) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_truck_info, null)
        
        viewModel.loadActiveShipment(driverId)
        
        val locationData = viewModel.locations.value?.find { 
            (it["driverId"] as? Double)?.toLong() == driverId 
        }
        
        if (locationData != null) {
            view.findViewById<TextView>(R.id.tvDriverName).text = "سائق #$driverId"
            view.findViewById<TextView>(R.id.tvSpeed).text = "${String.format("%.1f", locationData["speed"] ?: 0.0)} كم/س"
            view.findViewById<TextView>(R.id.tvTruckId).text = "شاحنة T-${String.format("%02d", driverId)}"
        }

        viewModel.activeShipment.observe(viewLifecycleOwner) { trip ->
            if (trip != null) {
                view.findViewById<TextView>(R.id.tvTruckId).text = "شاحنة ${trip.plateNumber ?: "T-$driverId"}"
                view.findViewById<TextView>(R.id.tvTempStatus).text = "${trip.temperature ?: "--"}°م"
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupRecyclerView(view: View) {
        val rvDrivers = view.findViewById<RecyclerView>(R.id.rvActiveDrivers)
        rvDrivers.layoutManager = LinearLayoutManager(requireContext())
        driverAdapter = DriverStatusAdapter(emptyList()) { driver ->
            focusOnDriver(driver.id)
        }
        rvDrivers.adapter = driverAdapter
    }

    private fun focusOnDriver(driverId: String) {
        val idLong = driverId.toLongOrNull() ?: return
        val marker = markers[idLong]
        if (marker != null) {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 12f))
            marker.showInfoWindow()
            showTruckInfoBottomSheet(idLong)
        } else {
            Toast.makeText(context, "لم يتم العثور على موقع للسائق حالياً", Toast.LENGTH_SHORT).show()
            viewModel.loadLocations() // Try refreshing
        }
    }

    private fun observeViewModel() {
        viewModel.drivers.observe(viewLifecycleOwner) { drivers ->
            val activeDrivers = drivers.filter { 
                it.status.uppercase() == "ACTIVE" || it.status.uppercase() == "IN_TRANSIT" 
            }
            driverAdapter.updateData(activeDrivers)
            tvActiveDriversCount?.text = "${activeDrivers.size} سائق نشط"
        }

        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            updateMarkers(locations)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMarkers(locations: List<Map<String, Any>>) {
        googleMap?.let { map ->
            locations.forEach { loc ->
                val id = (loc["driverId"] as? Double)?.toLong() ?: return@forEach
                val lat = loc["latitude"] as? Double ?: return@forEach
                val lng = loc["longitude"] as? Double ?: return@forEach
                val heading = (loc["heading"] as? Double)?.toFloat() ?: 0f
                val pos = LatLng(lat, lng)
                
                if (markers.containsKey(id)) {
                    val marker = markers[id]!!
                    animateMarkerSmoothly(marker, pos, heading)
                } else {
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title("سائق $id")
                            .anchor(0.5f, 0.5f)
                            .rotation(heading)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck))
                    )
                    marker?.tag = id
                    if (marker != null) {
                        markers[id] = marker
                    }
                }
            }
        }
    }

    private fun animateMarkerSmoothly(marker: Marker, toPosition: LatLng, heading: Float) {
        val startPosition = marker.position
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val start = android.os.SystemClock.uptimeMillis()
        val duration: Long = 2000 // duration of animation in ms

        val interpolator = android.view.animation.LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = android.os.SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val lng = t * toPosition.longitude + (1 - t) * startPosition.longitude
                val lat = t * toPosition.latitude + (1 - t) * startPosition.latitude

                marker.position = LatLng(lat, lng)
                
                // Smoothly rotate
                val rotationStep = t * heading + (1 - t) * marker.rotation
                marker.rotation = rotationStep

                if (t < 1.0) {
                    // Post again 16ms later (approx 60fps)
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    private fun startRealTimeTracking() {
        viewModel.loadDashboardData() 
        viewModel.loadLocations()
        
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                viewModel.loadDashboardData()
                viewModel.loadLocations()
            }
        }, 10000, 10000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
    }
}
