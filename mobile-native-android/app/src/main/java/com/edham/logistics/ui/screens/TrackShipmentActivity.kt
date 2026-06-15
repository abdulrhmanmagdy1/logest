package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.edham.logistics.Load
import com.edham.logistics.ui.home.customer.adapter.TimelineAdapter
import com.edham.logistics.ui.home.customer.adapter.TimelineStep
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@AndroidEntryPoint
class TrackShipmentActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: TrackShipmentViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var rvTimeline: RecyclerView
    private lateinit var timelineAdapter: TimelineAdapter
    private var activePolyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_shipment)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        rvTimeline = findViewById(R.id.rvTimeline)

        setupTimeline()
        
        val shipmentId = intent.getStringExtra("SHIPMENT_ID") ?: ""
        if (shipmentId.isNotEmpty()) {
            viewModel.startTracking(shipmentId)
        }

        observeViewModel()
        
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.trackMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeViewModel() {
        viewModel.shipment.observe(this) { load ->
            bindLoadData(load)
            updateMap(load)
        }
    }

    private fun bindLoadData(load: Load) {
        findViewById<TextView>(R.id.tvTrackId).text = load.id
        findViewById<TextView>(R.id.tvOrigin).text = load.from
        findViewById<TextView>(R.id.tvDest).text = load.to
        findViewById<TextView>(R.id.tvStatus).text = mapStatusToTitle(load.status)
        findViewById<TextView>(R.id.tvDistRem).text = "جاري الحساب..."
        findViewById<TextView>(R.id.tvTemp).text = load.temperature ?: "---"
        findViewById<TextView>(R.id.tvWeight).text = load.weight
        
        findViewById<TextView>(R.id.tvDriverName).text = load.driverName ?: "بانتظار التعيين"
        findViewById<TextView>(R.id.tvPlate).text = load.truckNumber ?: "---"
        findViewById<TextView>(R.id.tvDriverAvatar).text = load.driverName?.take(1) ?: "?"
        
        val tvRating = findViewById<TextView>(R.id.tvRating)
        tvRating.text = "★ 4.8" // In real app, load.driverRating
        tvRating.visibility = if (load.driverName != null) View.VISIBLE else View.GONE
        
        val btnRate = findViewById<View>(R.id.btnRateExperience)
        btnRate.visibility = if (load.status.uppercase() == "DELIVERED") View.VISIBLE else View.GONE
        btnRate.setOnClickListener {
            val feedbackFragment = com.edham.logistics.ui.home.customer.CustomerFeedbackFragment.newInstance(load.id)
            feedbackFragment.show(supportFragmentManager, "Feedback")
        }
        
        findViewById<View>(R.id.btnCall).setOnClickListener {
            load.driverPhone?.let { phone ->
                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
                intent.data = android.net.Uri.parse("tel:$phone")
                startActivity(intent)
            } ?: Toast.makeText(this, "رقم السائق غير متاح", Toast.LENGTH_SHORT).show()
        }
        
        // Update timeline from history
        val steps = load.statusHistory?.map { history ->
            TimelineStep(
                title = mapStatusToTitle(history.status),
                time = history.notes ?: history.timestamp,
                isCompleted = true,
                isActive = false
            )
        } ?: emptyList()
        
        timelineAdapter.updateData(steps)
    }

    private fun mapStatusToTitle(status: String): String = when(status.lowercase()) {
        "pending" -> "تم استلام الطلب"
        "assigned" -> "تم تعيين السائق"
        "picked_up" -> "تم التحميل"
        "on_the_way" -> "في الطريق"
        "delivered" -> "تم التسليم"
        else -> status
    }

    private fun setupTimeline() {
        timelineAdapter = TimelineAdapter(emptyList())
        rvTimeline.layoutManager = LinearLayoutManager(this)
        rvTimeline.adapter = timelineAdapter
    }

    private fun updateMap(load: Load) {
        googleMap?.clear()
        
        val origin = LatLng(load.pickupLat ?: 0.0, load.pickupLng ?: 0.0)
        val dest = LatLng(load.dropLat ?: 0.0, load.dropLng ?: 0.0)
        val truck = LatLng(load.currentLat ?: 0.0, load.currentLng ?: 0.0)

        if (origin.latitude != 0.0) {
            googleMap?.addMarker(MarkerOptions().position(origin).title("نقطة الاستلام"))
            googleMap?.addMarker(MarkerOptions().position(dest).title("نقطة التسليم"))
            
            googleMap?.addMarker(MarkerOptions()
                .position(truck)
                .title("موقع الشاحنة")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck)))
                
            drawRoute(origin, dest)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(truck, 10f))
        }
    }

    private fun drawRoute(origin: LatLng, dest: LatLng) {
        activePolyline?.remove()
        val polylineOptions = PolylineOptions()
            .add(origin, dest)
            .width(10f)
            .color(getColor(R.color.ed_teal_lighter))
            .startCap(RoundCap())
            .endCap(RoundCap())
            .geodesic(true)
        activePolyline = googleMap?.addPolyline(polylineOptions)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        try {
            googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark))
        } catch (e: Exception) {}
        
        viewModel.shipment.value?.let { updateMap(it) }
    }
}
