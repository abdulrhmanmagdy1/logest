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
import com.edham.logistics.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Supervisor fleet tracking screen - displays real-time fleet tracking
 * with all drivers and vehicles on the map
 */
class SupervisorFleetTrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_fleet_tracking)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.fleetContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Initialize map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        buildDashboard()
    }

    private fun buildDashboard() {
        // Fleet Overview Card
        val overviewCard = createOverviewCard()
        content.addView(overviewCard)

        // Active Drivers Card
        val driversCard = createDriversCard()
        content.addView(driversCard)
    }

    private fun createOverviewCard(): LinearLayout {
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
            text = "نظرة عامة على الأسطول"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        statsRow.addView(createStatItem("إجمالي المركبات", "45", Color.parseColor("#7BBDE8")))
        statsRow.addView(createStatItem("نشط حالياً", "38", Color.parseColor("#4CAF50")))
        statsRow.addView(createStatItem("متوقف", "7", Color.parseColor("#FFC107")))

        card.addView(statsRow)
        return card
    }

    private fun createStatItem(label: String, value: String, color: Int): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.CENTER
        }

        container.addView(TextView(this).apply {
            text = value
            setTextColor(color)
            textSize = 24f
            setTypeface(typeface, Typeface.BOLD)
        })

        container.addView(TextView(this).apply {
            text = label
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            setPadding(0, dp(4), 0, 0)
        })

        return container
    }

    private fun createDriversCard(): LinearLayout {
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
            text = "السائقون النشطون"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        val drivers = listOf(
            Driver("أحمد محمد", "مبرد-01", "نشط", "الرياض ← الدمام"),
            Driver("خالد العتيبي", "عادي-03", "نشط", "الدمام ← الخبر"),
            Driver("سعيد القحطاني", "سريع-05", "متوقف", "مكة ← جدة"),
            Driver("فهد الدوسري", "مبرد-02", "نشط", "جدة ← المدينة")
        )

        drivers.forEach { driver ->
            val driverRow = createDriverRow(driver)
            card.addView(driverRow)
        }

        return card
    }

    private fun createDriverRow(driver: Driver): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GradientDrawable().apply {
                cornerRadius = dp(8).toFloat()
                setColor(Color.parseColor("#0A2A4E"))
            }
            setPadding(dp(12), dp(12), dp(12), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }

        val infoContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        infoContainer.addView(TextView(this).apply {
            text = driver.name
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        })

        infoContainer.addView(TextView(this).apply {
            text = "${driver.vehicle} • ${driver.route}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            setPadding(0, dp(2), 0, 0)
        })

        row.addView(infoContainer)

        val statusColor = when (driver.status) {
            "نشط" -> Color.parseColor("#4CAF50")
            "متوقف" -> Color.parseColor("#FFC107")
            else -> Color.parseColor("#9E9E9E")
        }

        row.addView(TextView(this).apply {
            text = driver.status
            setTextColor(statusColor)
            textSize = 12f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(dp(8), dp(4), dp(8), dp(4))
            background = GradientDrawable().apply {
                cornerRadius = dp(8).toFloat()
                setColor(Color.parseColor("#1A1A1A"))
                setStroke(dp(1), statusColor)
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        return row
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        configureFleetMap(map)
    }

    private fun configureFleetMap(map: GoogleMap) {
        // Mock driver locations
        val drivers = listOf(
            LatLng(24.7136, 46.6753), // Riyadh
            LatLng(26.4207, 50.0839), // Dammam
            LatLng(21.4225, 39.8262), // Jeddah
            LatLng(24.5247, 39.5692)  // Madinah
        )

        // Clear previous markers
        map.clear()

        // Add markers for each driver
        drivers.forEachIndexed { index, location ->
            val markerColor = when (index) {
                0 -> BitmapDescriptorFactory.HUE_GREEN
                1 -> BitmapDescriptorFactory.HUE_BLUE
                2 -> BitmapDescriptorFactory.HUE_ORANGE
                else -> BitmapDescriptorFactory.HUE_RED
            }

            map.addMarker(MarkerOptions()
                .position(location)
                .title("السائق ${index + 1}")
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)))
        }

        // Move camera to show all drivers
        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
        drivers.forEach { bounds.include(it) }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}

data class Driver(
    val name: String,
    val vehicle: String,
    val status: String,
    val route: String
)
