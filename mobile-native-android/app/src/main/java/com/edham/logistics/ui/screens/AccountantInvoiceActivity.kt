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
import com.google.android.material.button.MaterialButton

/**
 * Accountant invoice generation screen - displays invoice list,
 * allows creating new invoices and managing existing ones
 */
class AccountantInvoiceActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountant_invoice)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.invoiceContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Overview Card
        val overviewCard = createOverviewCard()
        content.addView(overviewCard)

        // Invoices List
        val invoices = listOf(
            Invoice(
                id = "INV-2024-001",
                client = "شركة الأفق",
                amount = "2,500 ر.س",
                status = "Paid",
                date = "2024-05-15",
                dueDate = "2024-05-30"
            ),
            Invoice(
                id = "INV-2024-002",
                client = "شركة النور",
                amount = "1,800 ر.س",
                status = "Pending",
                date = "2024-05-18",
                dueDate = "2024-06-02"
            ),
            Invoice(
                id = "INV-2024-003",
                client = "شركة السلام",
                amount = "3,200 ر.س",
                status = "Overdue",
                date = "2024-05-10",
                dueDate = "2024-05-25"
            ),
            Invoice(
                id = "INV-2024-004",
                client = "شركة الرؤية",
                amount = "4,100 ر.س",
                status = "Paid",
                date = "2024-05-20",
                dueDate = "2024-06-04"
            )
        )

        invoices.forEach { invoice ->
            val invoiceCard = createInvoiceCard(invoice)
            content.addView(invoiceCard)
        }

        // Create New Invoice Button
        val createButton = MaterialButton(this).apply {
            text = "إنشاء فاتورة جديدة"
            setTextColor(Color.parseColor("#001D39"))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(16) }
            background = GradientDrawable().apply {
                cornerRadius = dp(12).toFloat()
                setColor(Color.parseColor("#7BBDE8"))
            }
            setOnClickListener {
                // Handle create new invoice
            }
        }
        content.addView(createButton)
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
            text = "نظرة عامة على الفواتير"
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

        statsRow.addView(createStatItem("إجمالي الفواتير", "45", Color.parseColor("#7BBDE8")))
        statsRow.addView(createStatItem("مدفوعة", "32", Color.parseColor("#4CAF50")))
        statsRow.addView(createStatItem("معلقة", "13", Color.parseColor("#FFC107")))

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

    private fun createInvoiceCard(invoice: Invoice): LinearLayout {
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
            text = invoice.id
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        headerRow.addView(TextView(this).apply {
            text = invoice.amount
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.END
        })

        card.addView(headerRow)

        // Details row
        card.addView(TextView(this).apply {
            text = "العميل: ${invoice.client}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(6), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "تاريخ الفاتورة: ${invoice.date} • تاريخ الاستحقاق: ${invoice.dueDate}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        // Status badge
        val statusColor = when (invoice.status) {
            "Paid" -> Color.parseColor("#4CAF50")
            "Pending" -> Color.parseColor("#FFC107")
            "Overdue" -> Color.parseColor("#F44336")
            else -> Color.parseColor("#9E9E9E")
        }

        val statusText = when (invoice.status) {
            "Paid" -> "مدفوعة"
            "Pending" -> "معلقة"
            "Overdue" -> "متأخرة"
            else -> invoice.status
        }

        card.addView(TextView(this).apply {
            text = statusText
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
            ).apply { topMargin = dp(8) }
        })

        return card
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}

data class Invoice(
    val id: String,
    val client: String,
    val amount: String,
    val status: String,
    val date: String,
    val dueDate: String
)
