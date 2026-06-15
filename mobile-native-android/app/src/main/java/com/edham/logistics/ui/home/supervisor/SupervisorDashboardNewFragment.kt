package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.api.ChartData as ApiChartData
import com.edham.logistics.core.network.api.SupervisorStats
import com.edham.logistics.ui.home.supervisor.adapter.DriverStatusAdapter
import com.edham.logistics.ui.home.supervisor.adapter.SmartAlertAdapter
import com.edham.logistics.ui.home.supervisor.adapter.SupervisorOrdersAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SupervisorDashboardNewFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var driverAdapter: DriverStatusAdapter
    private lateinit var alertAdapter: SmartAlertAdapter
    private lateinit var pendingOrdersAdapter: SupervisorOrdersAdapter
    private var googleMap: GoogleMap? = null
    private var activePolyline: Polyline? = null
    private val refreshTimer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_dashboard_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.findViewById<View>(R.id.btnMapType).setOnClickListener {
            googleMap?.let { map ->
                if (map.mapType == GoogleMap.MAP_TYPE_NORMAL) {
                    map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                } else {
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            }
        }

        view.findViewById<View>(R.id.btnRiskMatrix).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FleetRiskMatrixFragment())
                .addToBackStack(null)
                .commit()
        }

        setupRecyclerViews(view)
        observeViewModel(view)
        
        viewModel.loadDashboardData()
        viewModel.loadPendingOrders()
        startLocationPolling()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        try {
            val success = googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark)
            )
        } catch (e: Exception) {}

        googleMap?.setOnMarkerClickListener { marker ->
            val loc = viewModel.locations.value?.find { it["shipmentId"] == marker.tag }
            if (loc != null) {
                val bottomSheet = TruckInfoBottomSheet(loc) { shipmentId ->
                    val intent = android.content.Intent(requireContext(), SupervisorOrderDetailsActivity::class.java)
                    intent.putExtra("LOAD_ID", shipmentId)
                    startActivity(intent)
                }
                bottomSheet.show(childFragmentManager, "TruckInfo")
            }
            true
        }

        val saudi = LatLng(23.8859, 45.0792)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(saudi, 5.0f))
    }

    private fun drawTripRoute(truckPos: LatLng) {
        activePolyline?.remove()
        
        // Mock a route for demonstration (usually fetched from a Route API)
        // Here we draw a line to a fixed destination nearby for visual effect
        val dest = LatLng(truckPos.latitude + 0.5, truckPos.longitude + 0.5)
        
        val polylineOptions = PolylineOptions()
            .add(truckPos, dest)
            .width(12f)
            .color(ContextCompat.getColor(requireContext(), R.color.ed_teal_lighter))
            .startCap(RoundCap())
            .endCap(RoundCap())
            .geodesic(true)
            
        activePolyline = googleMap?.addPolyline(polylineOptions)
    }

    private fun startLocationPolling() {
        refreshTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                viewModel.loadLocations()
            }
        }, 0, 30000) // 30 seconds
    }

    override fun onDestroyView() {
        super.onDestroyView()
        refreshTimer.cancel()
    }

    private fun updateMapMarkers(locations: List<Map<String, Any>>) {
        googleMap?.clear()
        locations.forEach { loc ->
            val lat = loc["latitude"] as? Double ?: return@forEach
            val lng = loc["longitude"] as? Double ?: return@forEach
            val name = loc["driverName"] as? String ?: "سائق"
            val status = loc["status"] as? String ?: "نشط"
            val temp = loc["temperature"] as? Double
            val heading = (loc["heading"] as? Number)?.toFloat() ?: 0f
            val battery = loc["batteryLevel"] as? Number
            
            val pos = LatLng(lat, lng)
            
            // Determine status color based on data
            val truckStatus = when {
                status.uppercase() == "DELAYED" -> MapMarkerHelper.TruckStatus.DELAYED
                temp != null && temp > -5.0 -> MapMarkerHelper.TruckStatus.CRITICAL
                else -> MapMarkerHelper.TruckStatus.NORMAL
            }

            val snippet = StringBuilder("الحالة: $status")
            if (temp != null) snippet.append(" | $temp°م")
            if (battery != null) snippet.append(" | 🔋 $battery%")

            val markerOptions = MarkerOptions()
                .position(pos)
                .title(name)
                .snippet(snippet.toString())
                .anchor(0.5f, 0.5f)
                .icon(MapMarkerHelper.getTruckMarker(requireContext(), truckStatus, heading))
            
            val marker = googleMap?.addMarker(markerOptions)
            marker?.tag = loc["shipmentId"] // Store ID for click handling
        }
    }

    private fun setupRecyclerViews(view: View) {
        // Pending Orders
        val rvPending = view.findViewById<RecyclerView>(R.id.rvPendingOrders)
        pendingOrdersAdapter = SupervisorOrdersAdapter(emptyList()) { order ->
            val intent = android.content.Intent(requireContext(), SupervisorOrderDetailsActivity::class.java)
            intent.putExtra("LOAD_ID", order.id)
            intent.putExtra("LOAD_DATA", order) // Pass full object
            startActivity(intent)
        }
        rvPending.adapter = pendingOrdersAdapter

        // Leaderboard
        val rvLeaderboard = view.findViewById<RecyclerView>(R.id.rvLeaderboard)
        rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        driverAdapter = DriverStatusAdapter(emptyList()) { driver ->
            val intent = android.content.Intent(requireContext(), SupervisorDriverProfileActivity::class.java)
            // intent.putExtra("DRIVER_DATA", driver)
            startActivity(intent)
        }
        rvLeaderboard.adapter = driverAdapter

        // Smart Alerts
        val rvAlerts = view.findViewById<RecyclerView>(R.id.rvAlerts)
        rvAlerts.layoutManager = LinearLayoutManager(requireContext())
        alertAdapter = SmartAlertAdapter(emptyList()) { alert ->
            Toast.makeText(context, "التعامل مع التنبيه: ${alert.title}", Toast.LENGTH_SHORT).show()
        }
        rvAlerts.adapter = alertAdapter

        // Alert Filtering Logic
        view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.alertFilterGroup).setOnCheckedChangeListener { _, checkedId ->
            val alerts = viewModel.alerts.value ?: return@setOnCheckedChangeListener
            val filtered = when (checkedId) {
                R.id.chipHeatAlerts -> alerts.filter { it.type.uppercase() == "TEMPERATURE" }
                R.id.chipDelayAlerts -> alerts.filter { it.type.uppercase() == "DELAY" }
                else -> alerts
            }
            alertAdapter.updateData(filtered)
        }
    }

    private fun observeViewModel(view: View) {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.stat_total_trucks).text = stats.available_vehicles.toString()
            view.findViewById<TextView>(R.id.stat_active_trucks).text = stats.in_transit.toString()
            view.findViewById<TextView>(R.id.stat_loads).text = stats.delivered_today.toString()
            
            // Format revenue (e.g., 2.4M or 570K)
            val revenue = stats.total_earnings
            val revenueText = when {
                revenue >= 1_000_000 -> String.format("%.1fM", revenue / 1_000_000.0)
                revenue >= 1_000 -> String.format("%.0fK", revenue / 1_000.0)
                else -> revenue.toInt().toString()
            }
            view.findViewById<TextView>(R.id.stat_revenue).text = revenueText
            
            setupFleetChart(view.findViewById(R.id.fleetChart), stats)
        }

        viewModel.revenueChart.observe(viewLifecycleOwner) { data ->
            setupRevenueChart(view.findViewById(R.id.revenueChart), data)
        }

        viewModel.drivers.observe(viewLifecycleOwner) { drivers ->
            driverAdapter.updateData(drivers)
        }

        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            alertAdapter.updateData(alerts)
        }

        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            updateMapMarkers(locations)
        }

        viewModel.pendingOrders.observe(viewLifecycleOwner) { orders ->
            pendingOrdersAdapter.updateData(orders)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupFleetChart(chart: PieChart, stats: SupervisorStats) {
        val entries = listOf(
            PieEntry(stats.in_transit.toFloat(), "نشط"),
            PieEntry((stats.available_vehicles - stats.in_transit).toFloat(), "جاهز"),
            PieEntry(stats.maintenance_alerts.toFloat(), "صيانة")
        )
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        chart.data = PieData(dataSet)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setEntryLabelColor(android.graphics.Color.WHITE)
        chart.animateY(1000)
        chart.invalidate()
    }

    private fun setupRevenueChart(chart: BarChart, data: List<ApiChartData>) {
        val entries = data.mapIndexed { index, item -> BarEntry(index.toFloat(), item.value.toFloat()) }
        val dataSet = BarDataSet(entries, "الإيرادات")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.ed_teal_lighter)
        chart.data = BarData(dataSet)
        chart.description.isEnabled = false
        chart.animateY(1000)
        chart.invalidate()
    }
}
