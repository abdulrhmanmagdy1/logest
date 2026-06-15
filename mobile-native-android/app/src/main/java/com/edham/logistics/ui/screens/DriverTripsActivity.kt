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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.Load
import com.edham.logistics.R
import com.edham.logistics.core.di.ServiceLocator
import com.edham.logistics.core.network.api.ShipmentApi
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

/**
 * Driver trips screen - displays assigned trips with status update buttons.
 * Status flow: pending → at_pickup → picked_up → on_the_way → at_delivery → delivered → completed
 */
class DriverTripsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout
    private val shipmentApi: ShipmentApi = ServiceLocator.api<ShipmentApi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_trips)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.tripsContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        loadTrips()
    }

    private fun loadTrips() {
        lifecycleScope.launch {
            try {
                val response = shipmentApi.getShipments(limit = 50)
                if (response.isSuccessful && response.body() != null) {
                    val loads = response.body()!!.data?.data ?: emptyList()
                    content.removeAllViews()
                    if (loads.isEmpty()) {
                        content.addView(TextView(this@DriverTripsActivity).apply {
                            text = "لا توجد رحلات معينة حالياً"
                            setTextColor(Color.parseColor("#80FFFFFF"))
                            textSize = 14f
                            gravity = android.view.Gravity.CENTER
                            setPadding(0, dp(40), 0, dp(40))
                        })
                    } else {
                        loads.forEach { load ->
                            addTripCard(load)
                        }
                    }
                } else {
                    Toast.makeText(this@DriverTripsActivity, "فشل في تحميل الرحلات", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DriverTripsActivity, "خطأ: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTripCard(load: Load) {
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

        // Status badge
        val statusLabel = when (load.status.lowercase()) {
            "pending" -> "بانتظار التأكيد"
            "confirmed" -> "مؤكد"
            "assigned" -> "معينة لك"
            "at_pickup" -> "في موقع الاستلام"
            "picked_up" -> "تم الاستلام"
            "on_the_way" -> "في الطريق"
            "at_delivery" -> "في موقع التسليم"
            "delivered" -> "تم التسليم"
            "completed" -> "مكتملة"
            else -> load.status
        }
        val statusColor = when (load.status.lowercase()) {
            "pending" -> "#FFC107"
            "confirmed" -> "#7BBDE8"
            "assigned" -> "#4CAF50"
            "at_pickup", "picked_up", "on_the_way", "at_delivery" -> "#2196F3"
            "delivered", "completed" -> "#4CAF50"
            else -> "#9E9E9E"
        }

        // Header row
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        headerRow.addView(TextView(this).apply {
            text = load.id
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        headerRow.addView(TextView(this).apply {
            text = statusLabel
            setTextColor(Color.parseColor("#001D39"))
            textSize = 12f
            setTypeface(typeface, Typeface.BOLD)
            background = GradientDrawable().apply {
                cornerRadius = dp(10).toFloat()
                setColor(Color.parseColor(statusColor))
            }
            gravity = android.view.Gravity.CENTER
            setPadding(dp(10), dp(4), dp(10), dp(4))
        })
        card.addView(headerRow)

        // Details row
        card.addView(TextView(this).apply {
            text = "${load.from} ← ${load.to}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(8), 0, 0)
        })
        card.addView(TextView(this).apply {
            text = "الوزن: ${load.weight} • ${load.temperature ?: ""}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })
        card.addView(TextView(this).apply {
            text = "السائق: ${load.driverName ?: "غير معين"}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        // Status update buttons (only if assigned or in progress)
        if (load.status.lowercase() in listOf("assigned", "at_pickup", "picked_up", "on_the_way", "at_delivery")) {
            val nextStatus = getNextStatus(load.status)
            val actionsRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(12) }
            }

            actionsRow.addView(MaterialButton(this).apply {
                text = nextStatus.label
                setTextColor(Color.parseColor("#001D39"))
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                background = GradientDrawable().apply {
                    cornerRadius = dp(8).toFloat()
                    setColor(Color.parseColor("#7BBDE8"))
                }
                setOnClickListener {
                    updateShipmentStatus(load.id, nextStatus.value)
                }
            })

            card.addView(actionsRow)
        }

        content.addView(card)
    }

    private fun getNextStatus(currentStatus: String): StatusOption {
        return when (currentStatus.lowercase()) {
            "assigned" -> StatusOption("at_pickup", "وصلت موقع الاستلام")
            "at_pickup" -> StatusOption("picked_up", "تم استلام البضاعة")
            "picked_up" -> StatusOption("on_the_way", "في الطريق")
            "on_the_way" -> StatusOption("at_delivery", "وصلت موقع التسليم")
            "at_delivery" -> StatusOption("delivered", "تم التسليم")
            else -> StatusOption("completed", "إكمال")
        }
    }

    private fun updateShipmentStatus(shipmentId: String, newStatus: String) {
        lifecycleScope.launch {
            try {
                val response = shipmentApi.updateStatus(shipmentId, newStatus)
                if (response.isSuccessful) {
                    Toast.makeText(this@DriverTripsActivity, "تم تحديث الحالة بنجاح ✅", Toast.LENGTH_SHORT).show()
                    loadTrips() // Refresh
                } else {
                    Toast.makeText(this@DriverTripsActivity, "فشل التحديث: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DriverTripsActivity, "فشل التحديث: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class StatusOption(val value: String, val label: String)

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}

data class Trip(
    val id: String,
    val from: String,
    val to: String,
    val cargoType: String,
    val weight: String,
    val pickupTime: String,
    val price: String,
    val distance: String
)
