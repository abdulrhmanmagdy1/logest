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
 * Workshop parts management screen - displays parts inventory,
 * stock levels, and parts ordering
 */
class WorkshopPartsManagementActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_parts_management)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.partsContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Overview Card
        val overviewCard = createOverviewCard()
        content.addView(overviewCard)

        // Parts List
        val parts = listOf(
            Part(
                id = "PRT-001",
                name = "فلتر زيت",
                category = "صيانة دورية",
                stock = "45",
                minStock = "20",
                price = "150 ر.س"
            ),
            Part(
                id = "PRT-002",
                name = "إطارات أمامية",
                category = "إطارات",
                stock = "12",
                minStock = "10",
                price = "1,200 ر.س"
            ),
            Part(
                id = "PRT-003",
                name = "فرامل قرص",
                category = "فرامل",
                stock = "8",
                minStock = "15",
                price = "450 ر.س"
            ),
            Part(
                id = "PRT-004",
                name = "بطارية",
                category = "كهرباء",
                stock = "25",
                minStock = "10",
                price = "650 ر.س"
            ),
            Part(
                id = "PRT-005",
                name = "فلتر هواء",
                category = "صيانة دورية",
                stock = "60",
                minStock = "30",
                price = "85 ر.س"
            )
        )

        parts.forEach { part ->
            val partCard = createPartCard(part)
            content.addView(partCard)
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
            text = "نظرة عامة على القطع"
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

        statsRow.addView(createStatItem("إجمالي القطع", "245", Color.parseColor("#7BBDE8")))
        statsRow.addView(createStatItem("متوفر", "210", Color.parseColor("#4CAF50")))
        statsRow.addView(createStatItem("منخفض", "35", Color.parseColor("#FFC107")))

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
            textSize = 12f
            setPadding(0, dp(4), 0, 0)
        })

        return container
    }

    private fun createPartCard(part: Part): LinearLayout {
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
            text = part.id
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        val stockColor = if (part.stock.toInt() < part.minStock.toInt()) {
            Color.parseColor("#F44336")
        } else {
            Color.parseColor("#4CAF50")
        }

        headerRow.addView(TextView(this).apply {
            text = "${part.stock} قطعة"
            setTextColor(stockColor)
            textSize = 12f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(dp(8), dp(4), dp(8), dp(4))
            background = GradientDrawable().apply {
                cornerRadius = dp(8).toFloat()
                setColor(Color.parseColor("#1A1A1A"))
                setStroke(dp(1), stockColor)
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        card.addView(headerRow)

        // Details row
        card.addView(TextView(this).apply {
            text = part.name
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "التصنيف: ${part.category}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        // Stock info row
        val stockRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
        }

        stockRow.addView(TextView(this).apply {
            text = "الحد الأدنى: ${part.minStock}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        stockRow.addView(TextView(this).apply {
            text = "السعر: ${part.price}"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.END
        })

        card.addView(stockRow)

        return card
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}

data class Part(
    val id: String,
    val name: String,
    val category: String,
    val stock: String,
    val minStock: String,
    val price: String
)
