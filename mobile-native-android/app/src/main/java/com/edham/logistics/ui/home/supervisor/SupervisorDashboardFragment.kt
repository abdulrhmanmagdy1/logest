package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Supervisor main dashboard fragment — KPIs, status overview, active shipments.
 */
class SupervisorDashboardFragment : Fragment(), OnMapReadyCallback {

    private lateinit var rvActiveShipments: RecyclerView
    private lateinit var mapFleet: SupportMapFragment
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_supervisor_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvActiveShipments = view.findViewById(R.id.rvActiveShipments)
        rvActiveShipments.layoutManager = LinearLayoutManager(requireContext())
        // Adapter will be set when data is available from API

        // Initialize Google Maps
        mapFleet = childFragmentManager.findFragmentById(R.id.mapFleet) as SupportMapFragment
        mapFleet.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Default center on Saudi Arabia
        val saudiArabia = LatLng(23.8859, 45.0792)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(saudiArabia, 5f))

        // Add sample markers (will be replaced with real vehicle data from API)
        addSampleMarkers(map)
    }

    private fun addSampleMarkers(map: GoogleMap) {
        // Sample vehicle locations (will be replaced with real data)
        val riyadh = LatLng(24.7136, 46.6753)
        val jeddah = LatLng(21.5433, 39.1728)
        val dammam = LatLng(26.3929, 49.9777)

        map.addMarker(MarkerOptions().position(riyadh).title("سيارة 1 - الرياض"))
        map.addMarker(MarkerOptions().position(jeddah).title("سيارة 2 - جدة"))
        map.addMarker(MarkerOptions().position(dammam).title("سيارة 3 - الدمام"))
    }
}
