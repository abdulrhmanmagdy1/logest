package com.edham.logistics.ui.screens

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.edham.logistics.R
import com.edham.logistics.utils.GPSOptimizer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Driver GPS tracking screen - displays real-time GPS tracking
 * with location accuracy, speed, and optimization settings
 */
class DriverGPSTrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout
    private lateinit var gpsStatusText: TextView
    private lateinit var accuracyText: TextView
    private lateinit var speedText: TextView
    private lateinit var latitudeText: TextView
    private lateinit var longitudeText: TextView

    private var googleMap: GoogleMap? = null
    private var gpsOptimizer: GPSOptimizer? = null
    private var currentLocationMarker: MarkerOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_gps_tracking)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.gpsContent)
        gpsStatusText = findViewById(R.id.gpsStatusText)
        accuracyText = findViewById(R.id.accuracyText)
        speedText = findViewById(R.id.speedText)
        latitudeText = findViewById(R.id.latitudeText)
        longitudeText = findViewById(R.id.longitudeText)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Initialize GPS Optimizer
        gpsOptimizer = GPSOptimizer(this)
        gpsOptimizer?.optimizeForDriver()

        // Initialize map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Start GPS tracking
        startGPSTracking()

        buildDashboard()
    }

    private fun startGPSTracking() {
        gpsOptimizer?.startLocationUpdates()

        // Observe GPS status
        gpsOptimizer?.gpsStatus?.observe(this, Observer { status ->
            gpsStatusText.text = status
        })

        // Observe current location
        gpsOptimizer?.currentLocation?.observe(this, Observer { location ->
            updateLocationDisplay(location)
            updateMapLocation(location)
        })

        // Observe location accuracy
        gpsOptimizer?.locationAccuracy?.observe(this, Observer { accuracy ->
            accuracyText.text = "الدقة: ${accuracy} متر"
        })
    }

    private fun updateLocationDisplay(location: android.location.Location?) {
        location?.let {
            latitudeText.text = "خط العرض: ${it.latitude}"
            longitudeText.text = "خط الطول: ${it.longitude}"
            speedText.text = "السرعة: ${it.speed} م/ث"
        }
    }

    private fun updateMapLocation(location: android.location.Location?) {
        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            googleMap?.let { map ->
                // Clear previous markers
                map.clear()

                // Add new marker
                val marker = MarkerOptions()
                    .position(latLng)
                    .title("موقعك الحالي")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                map.addMarker(marker)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
    }

    private fun buildDashboard() {
        // GPS Status Card
        val statusCard = createInfoCard(
            "حالة GPS",
            listOf(
                "الوضع الحالي: نشط" to Color.parseColor("#4CAF50"),
                "مستوى التحسين: متوازن" to Color.parseColor("#7BBDE8"),
                "فترة التحديث: 5 ثواني" to Color.parseColor("#A0FFFFFF")
            )
        )
        content.addView(statusCard)

        // Optimization Info Card
        val optimizationCard = createInfoCard(
            "إعدادات التحسين",
            listOf(
                "High Performance" to Color.parseColor("#FFC107"),
                "Balanced" to Color.parseColor("#4CAF50"),
                "Power Saving" to Color.parseColor("#2196F3"),
                "Critical Power Saving" to Color.parseColor("#9E9E9E")
            )
        )
        content.addView(optimizationCard)
    }

    private fun createInfoCard(title: String, items: List<Pair<String, Int>>): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                setColor(Color.parseColor("#062E54"))
                setStroke(dp(1), Color.parseColor("#22FFFFFF"))
            }
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(12) }
        }

        card.addView(TextView(this).apply {
            text = title
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        items.forEach { (itemText, color) ->
            card.addView(TextView(this).apply {
                this.text = itemText
                setTextColor(color)
                textSize = 13f
                setPadding(0, dp(4), 0, 0)
            })
        }

        return card
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()

    override fun onDestroy() {
        super.onDestroy()
        gpsOptimizer?.stopLocationUpdates()
    }
}
