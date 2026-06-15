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
 * Service Alerts screen - automatic notifications for scheduled maintenance,
 * periodic service reminders, and preventive maintenance alerts.
 */
class ServiceAlertsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_generic)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.content)

        toolbar.title = "تنبيهات الخدمة الدورية"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // Summary card
        val summary = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg()
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = fullWidthWrap().apply { bottomMargin = dp(12) }
        }
        summary.addView(label("تنبيهات الخدمة", bold = true, size = 16f, color = Color.WHITE))
        summary.addView(spacer(8))
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; layoutParams = fullWidthWrap() }
        row.addView(statBox("تنبيهات عاجلة", "3", "#F44336"))
        row.addView(statBox("تنبيهات قريبة", "8", "#FFC107"))
        row.addView(statBox("مجدولة", "15", "#7BBDE8"))
        row.addView(statBox("تمت", "42", "#4CAF50"))
        summary.addView(row)
        content.addView(summary)

        // Urgent alerts
        content.addView(sectionTitle("تنبيهات عاجلة"))
        content.addView(alertCard(
            "VEH-005", "تغيير زيت متأخر", "مركبة ف و ق 4567 متأخرة 3 أيام عن موعد تغيير الزيت",
            "2026-05-22", "#F44336"
        ))
        content.addView(alertCard(
            "VEH-017", "كشف فرامل", "تنبيه دوري: كشف فرامل مركبة ط ا د 8899",
            "2026-05-23", "#F44336"
        ))
        content.addView(alertCard(
            "VEH-009", "فحص بطارية", "البطارية ضعيفة في مركبة س ع د 3344",
            "2026-05-23", "#F44336"
        ))

        // Upcoming alerts
        content.addView(sectionTitle("تنبيهات قريبة"))
        content.addView(alertCard(
            "VEH-012", "تغيير زيت", "موعد تغيير زيت مركبة ن ي س 7890 خلال 3 أيام",
            "2026-05-28", "#FFC107"
        ))
        content.addView(alertCard(
            "VEH-024", "صيانة دورية", "الصيانة الدورية لسكانيا ص ذ ر 5678",
            "2026-06-01", "#FFC107"
        ))
        content.addView(alertCard(
            "VEH-031", "فحص إطارات", "فحص ضغط الإطارات لمركبة خ ض غ 9012",
            "2026-06-03", "#FFC107"
        ))

        // Scheduled alerts
        content.addView(sectionTitle("مجدولة"))
        content.addView(alertCard(
            "VEH-018", "تغيير زيت", "تغيير زيت فولفو ط ا د 1234 بعد أسبوع",
            "2026-06-05", "#7BBDE8"
        ))
        content.addView(alertCard(
            "VEH-042", "صيانة شاملة", "الصيانة الشاملة لمركبة ز ح ط 5566",
            "2026-06-10", "#7BBDE8"
        ))
    }

    private fun alertCard(vehicleId: String, title: String, desc: String, date: String, color: String): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg()
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = fullWidthWrap().apply { bottomMargin = dp(10) }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = fullWidthWrap()
        }
        header.addView(label(vehicleId, bold = true, size = 14f, color = Color.WHITE, weight = 1f))
        header.addView(label(date, size = 11f, color = Color.parseColor(color)))
        card.addView(header)

        card.addView(label(title, bold = true, size = 15f, color = Color.parseColor(color), padTop = 8))
        card.addView(label(desc, size = 13f, color = Color.parseColor("#A0FFFFFF"), padTop = 4))

        val actionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = fullWidthWrap().apply { topMargin = dp(10) }
        }
        actionRow.addView(label("تم الإصلاح", size = 12f, color = Color.parseColor("#4CAF50"), weight = 1f, bg = "#1A1A1A"))
        actionRow.addView(label("تأجيل", size = 12f, color = Color.parseColor("#FFC107"), bg = "#1A1A1A"))
        card.addView(actionRow)

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
            addView(TextView(this@ServiceAlertsActivity).apply {
                this.text = value; setTextColor(Color.parseColor(color)); textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
            })
            addView(TextView(this@ServiceAlertsActivity).apply {
                this.text = title; setTextColor(Color.parseColor("#A0FFFFFF")); textSize = 11f
                setPadding(0, dp(4), 0, 0)
            })
        }
    }

    private fun sectionTitle(text: String): TextView = TextView(this).apply {
        this.text = text; setTextColor(Color.WHITE); textSize = 16f
        setTypeface(typeface, Typeface.BOLD); setPadding(0, dp(20), 0, dp(12))
    }

    private fun label(text: String, bold: Boolean = false, size: Float = 14f, color: Int = Color.WHITE,
                      weight: Float = 0f, bg: String? = null, padTop: Int = 0, gravity: Int = android.view.Gravity.START): TextView {
        return TextView(this).apply {
            this.text = text; setTextColor(color); textSize = size
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(padTop), 0, 0); this.gravity = gravity
            if (weight > 0) layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
            if (bg != null) {
                background = GradientDrawable().apply {
                    cornerRadius = dp(8).toFloat(); setColor(Color.parseColor(bg))
                }
                setPadding(dp(8), dp(4), dp(8), dp(4))
            }
        }
    }

    private fun spacer(dp: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(dp))
    }

    private fun fullWidthWrap(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()
}
