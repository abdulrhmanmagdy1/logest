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
 * Supervisor driver management screen - displays driver list,
 * performance metrics, and management options
 */
class SupervisorDriverManagementActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_driver_management)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.driversContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Overview Card
        val overviewCard = createOverviewCard()
        content.addView(overviewCard)

        // Drivers List
        val drivers = listOf(
            DriverInfo(
                name = "أحمد محمد",
                vehicle = "مبرد-01",
                status = "نشط",
                rating = "4.8",
                trips = "45",
                performance = "ممتاز"
            ),
            DriverInfo(
                name = "خالد العتيبي",
                vehicle = "عادي-03",
                status = "نشط",
                rating = "4.5",
                trips = "38",
                performance = "جيد"
            ),
            DriverInfo(
                name = "سعيد القحطاني",
                vehicle = "سريع-05",
                status = "متوقف",
                rating = "4.2",
                trips = "52",
                performance = "جيد"
            ),
            DriverInfo(
                name = "فهد الدوسري",
                vehicle = "مبرد-02",
                status = "نشط",
                rating = "4.9",
                trips = "67",
                performance = "ممتاز"
            ),
            DriverInfo(
                name = "عبدالله الشمري",
                vehicle = "عادي-07",
                status = "في إجازة",
                rating = "4.6",
                trips = "41",
                performance = "جيد"
            )
        )

        drivers.forEach { driver ->
            val driverCard = createDriverCard(driver)
            content.addView(driverCard)
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
            text = "نظرة عامة على السائقين"
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

        statsRow.addView(createStatItem("إجمالي السائقين", "45", Color.parseColor("#7BBDE8")))
        statsRow.addView(createStatItem("نشط حالياً", "38", Color.parseColor("#4CAF50")))
        statsRow.addView(createStatItem("متوسط التقييم", "4.7 ⭐", Color.parseColor("#FFC107")))

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

    private fun createDriverCard(driver: DriverInfo): LinearLayout {
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
            text = driver.name
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        val statusColor = when (driver.status) {
            "نشط" -> Color.parseColor("#4CAF50")
            "متوقف" -> Color.parseColor("#FFC107")
            "في إجازة" -> Color.parseColor("#9E9E9E")
            else -> Color.parseColor("#9E9E9E")
        }

        headerRow.addView(TextView(this).apply {
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

        card.addView(headerRow)

        // Details row
        card.addView(TextView(this).apply {
            text = "المركبة: ${driver.vehicle}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(6), 0, 0)
        })

        // Metrics row
        val metricsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
        }

        metricsRow.addView(TextView(this).apply {
            text = "التقييم: ${driver.rating} ⭐"
            setTextColor(Color.parseColor("#FFC107"))
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        metricsRow.addView(TextView(this).apply {
            text = "الرحلات: ${driver.trips}"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        card.addView(metricsRow)

        // Performance row
        val performanceColor = when (driver.performance) {
            "ممتاز" -> Color.parseColor("#4CAF50")
            "جيد" -> Color.parseColor("#2196F3")
            else -> Color.parseColor("#FFC107")
        }

        card.addView(TextView(this).apply {
            text = "الأداء: ${driver.performance}"
            setTextColor(performanceColor)
            textSize = 13f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(4), 0, 0)
        })

        return card
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}

data class DriverInfo(
    val name: String,
    val vehicle: String,
    val status: String,
    val rating: String,
    val trips: String,
    val performance: String
)
