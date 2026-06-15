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

/**
 * Workshop vehicle maintenance screen - displays vehicle maintenance schedule,
 * service history, and maintenance status
 */
class WorkshopVehicleMaintenanceActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_vehicle_maintenance)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.maintenanceContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Overview Card
        val overviewCard = createOverviewCard()
        content.addView(overviewCard)

        // Vehicles List
        val vehicles = listOf(
            Vehicle(
                id = "VEH-001",
                plate = "أ ب ج 1234",
                model = "هيوندا HD78",
                status = "نشط",
                lastService = "2024-05-10",
                nextService = "2024-06-10",
                mileage = "45,230 كم"
            ),
            Vehicle(
                id = "VEH-002",
                plate = "س ع د 5678",
                model = "فولفو FH16",
                status = "في الصيانة",
                lastService = "2024-05-15",
                nextService = "2024-06-15",
                mileage = "78,450 كم"
            ),
            Vehicle(
                id = "VEH-003",
                plate = "ر س ط 9012",
                model = "مان TGS",
                status = "نشط",
                lastService = "2024-05-05",
                nextService = "2024-06-05",
                mileage = "62,100 كم"
            ),
            Vehicle(
                id = "VEH-004",
                plate = "ل م ن 3456",
                model = "سكانيا R500",
                status = "متوقف",
                lastService = "2024-04-20",
                nextService = "2024-05-20",
                mileage = "89,780 كم"
            )
        )

        vehicles.forEach { vehicle ->
            val vehicleCard = createVehicleCard(vehicle)
            content.addView(vehicleCard)
        }
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
            text = "نظرة عامة على الصيانة"
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
        statsRow.addView(createStatItem("نشط", "38", Color.parseColor("#4CAF50")))
        statsRow.addView(createStatItem("في الصيانة", "4", Color.parseColor("#FFC107")))
        statsRow.addView(createStatItem("متوقف", "3", Color.parseColor("#9E9E9E")))

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
            textSize = 20f
            setTypeface(typeface, Typeface.BOLD)
        })

        container.addView(TextView(this).apply {
            text = label
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 11f
            setPadding(0, dp(4), 0, 0)
        })

        return container
    }

    private fun createVehicleCard(vehicle: Vehicle): LinearLayout {
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

        // Header row
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        headerRow.addView(TextView(this).apply {
            text = vehicle.id
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        val statusColor = when (vehicle.status) {
            "نشط" -> Color.parseColor("#4CAF50")
            "في الصيانة" -> Color.parseColor("#FFC107")
            "متوقف" -> Color.parseColor("#9E9E9E")
            else -> Color.parseColor("#9E9E9E")
        }

        headerRow.addView(TextView(this).apply {
            text = vehicle.status
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

        card.addView(headerRow)

        // Details row
        card.addView(TextView(this).apply {
            text = "${vehicle.plate} • ${vehicle.model}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(6), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "المسافة: ${vehicle.mileage}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        // Service info row
        val serviceRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
        }

        serviceRow.addView(TextView(this).apply {
            text = "آخر صيانة: ${vehicle.lastService}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        serviceRow.addView(TextView(this).apply {
            text = "التالية: ${vehicle.nextService}"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.END
        })

        card.addView(serviceRow)

        return card
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}

data class Vehicle(
    val id: String,
    val plate: String,
    val model: String,
    val status: String,
    val lastService: String,
    val nextService: String,
    val mileage: String
)
