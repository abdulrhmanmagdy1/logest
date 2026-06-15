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
 * Supervisor analytics screen - displays performance metrics,
 * charts, and reports for fleet operations
 */
class SupervisorAnalyticsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_analytics)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.analyticsContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Performance Overview Card
        val performanceCard = createPerformanceCard()
        content.addView(performanceCard)

        // Fleet Efficiency Card
        val efficiencyCard = createEfficiencyCard()
        content.addView(efficiencyCard)

        // Revenue Card
        val revenueCard = createRevenueCard()
        content.addView(revenueCard)

        // Trends Card
        val trendsCard = createTrendsCard()
        content.addView(trendsCard)
    }

    private fun createPerformanceCard(): LinearLayout {
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
            text = "أداء الأسطول"
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

        statsRow.addView(createStatItem("الرحلات المكتملة", "245", Color.parseColor("#4CAF50")))
        statsRow.addView(createStatItem("في الوقت", "92%", Color.parseColor("#7BBDE8")))
        statsRow.addView(createStatItem("التأخيرات", "8%", Color.parseColor("#FFC107")))

        card.addView(statsRow)
        return card
    }

    private fun createEfficiencyCard(): LinearLayout {
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
            text = "كفاءة الأسطول"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        val metrics = listOf(
            "استغلال المركبات" to "87%",
            "متوسط المسافة اليومية" to "450 كم",
            "استهلاك الوقود" to "12.5 ل/100 كم",
            "أوقات التوقف" to "1.2 س/يوم"
        )

        metrics.forEach { (label, value) ->
            val metricRow = createMetricRow(label, value)
            card.addView(metricRow)
        }

        return card
    }

    private fun createRevenueCard(): LinearLayout {
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
            text = "الإيرادات (هذا الشهر)"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        card.addView(TextView(this).apply {
            text = "245,000 ر.س"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 24f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(4), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "↑ 12% عن الشهر الماضي"
            setTextColor(Color.parseColor("#4CAF50"))
            textSize = 13f
            setPadding(0, dp(4), 0, 0)
        })

        val breakdownRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }

        breakdownRow.addView(createStatItem("نقل مبرد", "45%", Color.parseColor("#2196F3")))
        breakdownRow.addView(createStatItem("شحن سريع", "35%", Color.parseColor("#FFC107")))
        breakdownRow.addView(createStatItem("نقل عادي", "20%", Color.parseColor("#4CAF50")))

        card.addView(breakdownRow)
        return card
    }

    private fun createTrendsCard(): LinearLayout {
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
            text = "الاتجاهات (آخر 6 أشهر)"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        val trends = listOf(
            "يناير" to "180,000",
            "فبراير" to "195,000",
            "مارس" to "210,000",
            "أبريل" to "225,000",
            "مايو" to "235,000",
            "يونيو" to "245,000"
        )

        trends.forEach { (month, revenue) ->
            val trendRow = createTrendRow(month, revenue)
            card.addView(trendRow)
        }

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

    private fun createMetricRow(label: String, value: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
        }

        row.addView(TextView(this).apply {
            text = label
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        row.addView(TextView(this).apply {
            text = value
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 13f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        return row
    }

    private fun createTrendRow(month: String, revenue: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(4) }
        }

        row.addView(TextView(this).apply {
            text = month
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        row.addView(View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, dp(1), 1f)
            background = GradientDrawable().apply {
                cornerRadius = dp(1).toFloat()
                setColor(Color.parseColor("#22FFFFFF"))
            }
        })

        row.addView(TextView(this).apply {
            text = "${revenue} ر.س"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 12f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        return row
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}
