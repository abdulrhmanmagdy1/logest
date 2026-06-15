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
import com.google.android.gms.maps.model.PolylineOptions

/**
 * Driver navigation screen - displays turn-by-turn navigation
 * with route information and destination details
 */
class DriverNavigationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_navigation)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.navigationContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Initialize map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        buildDashboard()
    }

    private fun buildDashboard() {
        // Route Info Card
        val routeCard = createRouteCard()
        content.addView(routeCard)

        // Turn Instructions Card
        val turnsCard = createTurnsCard()
        content.addView(turnsCard)

        // Destination Card
        val destinationCard = createDestinationCard()
        content.addView(destinationCard)
    }

    private fun createRouteCard(): LinearLayout {
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
            text = "مسار الرحلة"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        card.addView(TextView(this).apply {
            text = "الرياض ← الدمام"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 14f
            setPadding(0, dp(4), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "المسافة: 450 كم • المدة: 4 س 30 د"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        return card
    }

    private fun createTurnsCard(): LinearLayout {
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
            text = "التعليمات"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        val turns = listOf(
            "انعطف يميناً بعد 500 م" to "↗",
            "استمر على الطريق لمدة 3 كم" to "↑",
            "انعطف يساراً عند التقاطع" to "↖",
            "استمر لمدة 150 كم" to "↑",
            "انعطف يميناً نحو الدمام" to "↗"
        )

        turns.forEach { (instruction, arrow) ->
            val turnRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(6) }
            }

            turnRow.addView(TextView(this).apply {
                text = arrow
                setTextColor(Color.parseColor("#7BBDE8"))
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })

            turnRow.addView(TextView(this).apply {
                text = instruction
                setTextColor(Color.parseColor("#A0FFFFFF"))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { marginStart = dp(12) }
            })

            card.addView(turnRow)
        }

        return card
    }

    private fun createDestinationCard(): LinearLayout {
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
            text = "الوجهة"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        card.addView(TextView(this).apply {
            text = "الدمام - مركز التوزيع"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 14f
            setPadding(0, dp(4), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "العنوان: طريق الملك فهد، حي النخيل"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "رقم الهاتف: 013-123-4567"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        return card
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        configureNavigationRoute(map)
    }

    private fun configureNavigationRoute(map: GoogleMap) {
        // Mock route: Riyadh to Dammam
        val riyadh = LatLng(24.7136, 46.6753)
        val dammam = LatLng(26.4207, 50.0839)
        val currentLocation = LatLng(25.0, 47.5) // Mock current position

        // Clear previous markers and polylines
        map.clear()

        // Add origin marker (Riyadh)
        map.addMarker(MarkerOptions()
            .position(riyadh)
            .title("الرياض - نقطة الانطلاق")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        // Add destination marker (Dammam)
        map.addMarker(MarkerOptions()
            .position(dammam)
            .title("الدمام - الوجهة")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        // Add current location marker
        map.addMarker(MarkerOptions()
            .position(currentLocation)
            .title("موقعك الحالي")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

        // Draw polyline for the route
        val polylineOptions = PolylineOptions()
            .add(riyadh)
            .add(currentLocation)
            .add(dammam)
            .width(8f)
            .color(Color.parseColor("#7BBDE8"))
            .geodesic(true)
        map.addPolyline(polylineOptions)

        // Move camera to show the entire route
        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
            .include(riyadh)
            .include(currentLocation)
            .include(dammam)
            .build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}
