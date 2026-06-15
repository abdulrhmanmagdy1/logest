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
 * Accountant financial reports screen - displays financial reports,
 * revenue, expenses, and profit analysis
 */
class AccountantFinancialReportsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountant_financial_reports)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.reportsContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Revenue Card
        val revenueCard = createRevenueCard()
        content.addView(revenueCard)

        // Expenses Card
        val expensesCard = createExpensesCard()
        content.addView(expensesCard)

        // Profit Card
        val profitCard = createProfitCard()
        content.addView(profitCard)

        // Monthly Trends Card
        val trendsCard = createTrendsCard()
        content.addView(trendsCard)
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

    private fun createExpensesCard(): LinearLayout {
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
            text = "المصاريف (هذا الشهر)"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        card.addView(TextView(this).apply {
            text = "128,500 ر.س"
            setTextColor(Color.parseColor("#F44336"))
            textSize = 24f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(4), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "↓ 5% عن الشهر الماضي"
            setTextColor(Color.parseColor("#4CAF50"))
            textSize = 13f
            setPadding(0, dp(4), 0, 0)
        })

        val expenses = listOf(
            "الوقود" to "45%",
            "الصيانة" to "25%",
            "الرواتب" to "20%",
            "أخرى" to "10%"
        )

        expenses.forEach { (expense, percentage) ->
            val expenseRow = createExpenseRow(expense, percentage)
            card.addView(expenseRow)
        }

        return card
    }

    private fun createProfitCard(): LinearLayout {
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
            text = "الأرباح الصافية"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        card.addView(TextView(this).apply {
            text = "116,500 ر.س"
            setTextColor(Color.parseColor("#4CAF50"))
            textSize = 28f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(4), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "هامش الربح: 47.5%"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 14f
            setPadding(0, dp(4), 0, 0)
        })

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
            text = "الاتجاهات المالية (آخر 6 أشهر)"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        val trends = listOf(
            "يناير" to Pair("180,000", "145,000"),
            "فبراير" to Pair("195,000", "152,500"),
            "مارس" to Pair("210,000", "158,000"),
            "أبريل" to Pair("225,000", "165,000"),
            "مايو" to Pair("235,000", "172,500"),
            "يونيو" to Pair("245,000", "128,500")
        )

        trends.forEach { (month, values) ->
            val trendRow = createTrendRow(month, values.first, values.second)
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

    private fun createExpenseRow(expense: String, percentage: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
        }

        row.addView(TextView(this).apply {
            text = expense
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        row.addView(View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, dp(1), 1f)
            background = GradientDrawable().apply {
                cornerRadius = dp(1).toFloat()
                setColor(Color.parseColor("#22FFFFFF"))
            }
        })

        row.addView(TextView(this).apply {
            text = percentage
            setTextColor(Color.parseColor("#F44336"))
            textSize = 13f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        return row
    }

    private fun createTrendRow(month: String, revenue: String, expenses: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(4) }
        }

        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        headerRow.addView(TextView(this).apply {
            text = month
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        headerRow.addView(View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, dp(1), 1f)
            background = GradientDrawable().apply {
                cornerRadius = dp(1).toFloat()
                setColor(Color.parseColor("#22FFFFFF"))
            }
        })

        headerRow.addView(TextView(this).apply {
            text = "الإيرادات: ${revenue} ر.س"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 12f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        row.addView(headerRow)

        row.addView(TextView(this).apply {
            text = "المصاريف: ${expenses} ر.س"
            setTextColor(Color.parseColor("#F44336"))
            textSize = 11f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = dp(80) }
        })

        return row
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}
