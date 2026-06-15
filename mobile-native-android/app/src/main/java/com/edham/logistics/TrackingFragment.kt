package com.edham.logistics

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class TrackingFragment : Fragment(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var mapShipment: SupportMapFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTrackingId = view.findViewById<EditText>(R.id.et_tracking_id)
        val btnTrack = view.findViewById<Button>(R.id.btn_track)
        val trackingResult = view.findViewById<LinearLayout>(R.id.tracking_result)

        // Initialize Google Maps
        mapShipment = childFragmentManager.findFragmentById(R.id.mapShipment) as? SupportMapFragment
        mapShipment?.getMapAsync(this)

        // Auto-enable track button when text is entered
        etTrackingId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                btnTrack.isEnabled = !s.isNullOrEmpty()
            }
        })

        btnTrack.setOnClickListener {
            val trackingId = etTrackingId.text.toString()
            if (trackingId.isNotEmpty()) {
                showTrackingResult(view, trackingId)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Default center on Saudi Arabia
        val saudiArabia = LatLng(23.8859, 45.0792)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(saudiArabia, 5f))
    }

    private fun showTrackingResult(view: View, trackingId: String) {
        val trackingResult = view.findViewById<LinearLayout>(R.id.tracking_result)
        val tvTrackingId = view.findViewById<TextView>(R.id.tv_tracking_id)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)

        // Update tracking info
        tvTrackingId.text = trackingId

        // Simulate tracking lookup
        val status = when {
            trackingId.contains("001") -> "In Transit"
            trackingId.contains("002") -> "Delivered"
            trackingId.contains("003") -> "Pending"
            else -> "Not Found"
        }

        tvStatus.text = status
        tvStatus.background = when (status) {
            "In Transit" -> resources.getDrawable(R.drawable.bg_status_transit)
            "Delivered" -> resources.getDrawable(R.drawable.bg_status_completed)
            "Pending" -> resources.getDrawable(R.drawable.bg_status_pending)
            else -> resources.getDrawable(R.drawable.bg_status_cancelled)
        }

        trackingResult.visibility = View.VISIBLE

        // Show shipment route on map (sample route from Riyadh to Jeddah)
        showShipmentRoute()
    }

    private fun showShipmentRoute() {
        googleMap?.let { map ->
            // Sample route from Riyadh to Jeddah
            val riyadh = LatLng(24.7136, 46.6753)
            val jeddah = LatLng(21.5433, 39.1728)

            // Add markers
            map.addMarker(MarkerOptions().position(riyadh).title("الرياض - نقطة البداية"))
            map.addMarker(MarkerOptions().position(jeddah).title("جدة - نقطة الوصول"))

            // Draw polyline
            val polyline = map.addPolyline(
                PolylineOptions()
                    .add(riyadh)
                    .add(LatLng(23.5, 44.5))
                    .add(LatLng(22.5, 42.5))
                    .add(jeddah)
                    .width(5f)
                    .color(0xFF7BBDE8.toInt())
            )

            // Move camera to show route
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(riyadh, 6f))
        }
    }
}
