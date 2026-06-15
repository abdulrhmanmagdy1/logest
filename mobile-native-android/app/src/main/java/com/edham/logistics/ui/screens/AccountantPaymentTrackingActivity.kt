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
 * Accountant payment tracking screen - displays payment history,
 * pending payments, and payment status for invoices
 */
class AccountantPaymentTrackingActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountant_payment_tracking)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.paymentContent)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Overview Card
        val overviewCard = createOverviewCard()
        content.addView(overviewCard)

        // Payment Methods Card
        val methodsCard = createPaymentMethodsCard()
        content.addView(methodsCard)

        // Recent Payments List
        val payments = listOf(
            Payment(
                id = "PAY-2024-001",
                invoiceId = "INV-2024-001",
                client = "شركة الأفق",
                amount = "2,500 ر.س",
                method = "تحويل بنكي",
                date = "2024-05-15",
                status = "Completed"
            ),
            Payment(
                id = "PAY-2024-002",
                invoiceId = "INV-2024-002",
                client = "شركة النور",
                amount = "1,800 ر.س",
                method = "مدى",
                date = "2024-05-18",
                status = "Pending"
            ),
            Payment(
                id = "PAY-2024-003",
                invoiceId = "INV-2024-003",
                client = "شركة السلام",
                amount = "3,200 ر.س",
                method = "شيك",
                date = "2024-05-10",
                status = "Overdue"
            ),
            Payment(
                id = "PAY-2024-004",
                invoiceId = "INV-2024-004",
                client = "شركة الرؤية",
                amount = "4,100 ر.س",
                method = "تحويل بنكي",
                date = "2024-05-20",
                status = "Completed"
            )
        )

        payments.forEach { payment ->
            val paymentCard = createPaymentCard(payment)
            content.addView(paymentCard)
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
            text = "نظرة عامة على المدفوعات"
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

        statsRow.addView(createStatItem("إجمالي المدفوعات", "45", Color.parseColor("#7BBDE8")))
        statsRow.addView(createStatItem("مكتملة", "32", Color.parseColor("#4CAF50")))
        statsRow.addView(createStatItem("معلقة", "13", Color.parseColor("#FFC107")))

        card.addView(statsRow)
        return card
    }

    private fun createPaymentMethodsCard(): LinearLayout {
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
            text = "طرق الدفع"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })

        val methods = listOf(
            "تحويل بنكي" to "45%",
            "مدى" to "35%",
            "شيك" to "15%",
            "نقد" to "5%"
        )

        methods.forEach { (method, percentage) ->
            val methodRow = createMethodRow(method, percentage)
            card.addView(methodRow)
        }

        return card
    }

    private fun createMethodRow(method: String, percentage: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
        }

        row.addView(TextView(this).apply {
            text = method
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

    private fun createPaymentCard(payment: Payment): LinearLayout {
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
            text = payment.id
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        headerRow.addView(TextView(this).apply {
            text = payment.amount
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.END
        })

        card.addView(headerRow)

        // Details row
        card.addView(TextView(this).apply {
            text = "العميل: ${payment.client} • الفاتورة: ${payment.invoiceId}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(6), 0, 0)
        })

        card.addView(TextView(this).apply {
            text = "طريقة الدفع: ${payment.method} • التاريخ: ${payment.date}"
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(2), 0, 0)
        })

        // Status badge
        val statusColor = when (payment.status) {
            "Completed" -> Color.parseColor("#4CAF50")
            "Pending" -> Color.parseColor("#FFC107")
            "Overdue" -> Color.parseColor("#F44336")
            else -> Color.parseColor("#9E9E9E")
        }

        val statusText = when (payment.status) {
            "Completed" -> "مكتمل"
            "Pending" -> "معلق"
            "Overdue" -> "متأخر"
            else -> payment.status
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

data class Payment(
    val id: String,
    val invoiceId: String,
    val client: String,
    val amount: String,
    val method: String,
    val date: String,
    val status: String
)
