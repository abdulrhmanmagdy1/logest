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
 * Oil Change Tracking screen - tracks oil change schedules for all fleet vehicles.
 * Ensures vehicles maintain peak performance and reduce breakdowns.
 */
class OilChangeTrackingActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_generic)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.content)

        toolbar.title = "تتبع تغيير الزيت"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Summary card
        val summaryCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg()
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = fullWidthWrap().apply { bottomMargin = dp(12) }
        }

        summaryCard.addView(label("نظرة عامة على تغيير الزيت", bold = true, size = 16f, color = Color.WHITE))
        summaryCard.addView(spacer(8))

        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = fullWidthWrap()
        }
        statsRow.addView(statBox("إجمالي المركبات", "45", "#7BBDE8"))
        statsRow.addView(statBox("تم تغيير الزيت", "38", "#4CAF50"))
        statsRow.addView(statBox("بانتظار التغيير", "5", "#FFC107"))
        statsRow.addView(statBox("متأخر", "2", "#F44336"))
        summaryCard.addView(statsRow)
        content.addView(summaryCard)

        // Vehicles due for oil change
        content.addView(sectionTitle("مركبات تنتظر تغيير الزيت"))

        val dueVehicles = listOf(
            OilChangeVehicle("VEH-005", "ف و ق 4567", "هيونداي HD78", "2026-05-25", "48,200 كم", "متأخر 3 أيام", "#F44336"),
            OilChangeVehicle("VEH-012", "ن ي س 7890", "مان TGS", "2026-05-28", "55,800 كم", "خلال 3 أيام", "#FFC107"),
            OilChangeVehicle("VEH-018", "ط ا د 1234", "فولفو FH16", "2026-06-01", "62,300 كم", "خلال أسبوع", "#7BBDE8"),
            OilChangeVehicle("VEH-024", "ص ذ ر 5678", "سكانيا R500", "2026-06-05", "71,100 كم", "خلال 10 أيام", "#7BBDE8"),
            OilChangeVehicle("VEH-031", "خ ض غ 9012", "هيونداي HD78", "2026-06-10", "38,500 كم", "خلال أسبوعين", "#7BBDE8")
        )

        dueVehicles.forEach { v ->
            content.addView(createVehicleOilCard(v))
        }
    }

    private fun createVehicleOilCard(v: OilChangeVehicle): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg()
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = fullWidthWrap().apply { bottomMargin = dp(12) }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = fullWidthWrap()
        }
        header.addView(label(v.id, bold = true, size = 16f, color = Color.WHITE, weight = 1f))
        header.addView(label(v.status, bold = true, size = 12f, color = Color.parseColor(v.statusColor), bg = "#1A1A1A", stroke = v.statusColor))
        card.addView(header)

        card.addView(label("${v.plate} \u2022 ${v.model}", size = 13f, color = Color.parseColor("#A0FFFFFF"), padTop = 6))
        card.addView(label("المسافة الحالية: ${v.mileage}", size = 13f, color = Color.parseColor("#A0FFFFFF"), padTop = 2))

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = fullWidthWrap().apply { topMargin = dp(8) }
        }
        row.addView(label("موعد التغيير: ${v.dueDate}", size = 12f, color = Color.parseColor("#A0FFFFFF"), weight = 1f))
        row.addView(label("تغيير الزيت الآن", size = 12f, color = Color.parseColor("#7BBDE8"), gravity = android.view.Gravity.END))
        card.addView(row)

        return card
    }

    private fun cardBg(): GradientDrawable = GradientDrawable().apply {
        cornerRadius = dp(16).toFloat()
        setColor(Color.parseColor("#062E54"))
        setStroke(dp(1), Color.parseColor("#22FFFFFF"))
    }

    private fun statBox(title: String, value: String, color: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.CENTER

            addView(TextView(this@OilChangeTrackingActivity).apply {
                this.text = value
                setTextColor(Color.parseColor(color))
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
            })
            addView(TextView(this@OilChangeTrackingActivity).apply {
                this.text = title
                setTextColor(Color.parseColor("#A0FFFFFF"))
                textSize = 11f
                setPadding(0, dp(4), 0, 0)
            })
        }
    }

    private fun sectionTitle(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(20), 0, dp(12))
        }
    }

    private fun label(
        text: String, bold: Boolean = false, size: Float = 14f,
        color: Int = Color.WHITE, weight: Float = 0f,
        bg: String? = null, stroke: String? = null,
        padTop: Int = 0, gravity: Int = android.view.Gravity.START
    ): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(color)
            textSize = size
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(padTop), 0, 0)
            this.gravity = gravity
            if (weight > 0) layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
            if (bg != null) {
                background = GradientDrawable().apply {
                    cornerRadius = dp(8).toFloat()
                    setColor(Color.parseColor(bg))
                    if (stroke != null) setStroke(dp(1), Color.parseColor(stroke))
                }
                setPadding(dp(8), dp(4), dp(8), dp(4))
            }
        }
    }

    private fun spacer(dp: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(dp))
        }
    }

    private fun fullWidthWrap(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}

data class OilChangeVehicle(
    val id: String,
    val plate: String,
    val model: String,
    val dueDate: String,
    val mileage: String,
    val status: String,
    val statusColor: String
)
