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
 * Breakdown Reduction & Fleet Efficiency screen.
 * Shows strategies, KPIs, and analytics to extend fleet lifespan
 * and reduce unexpected breakdowns.
 */
class BreakdownReductionActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_generic)

        toolbar = findViewById(R.id.toolbar)
        content = findViewById(R.id.content)

        toolbar.title = "تقليل الأعطال وتحسين الإنتاجية"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        buildDashboard()
    }

    private fun buildDashboard() {
        // KPIs summary
        val kpiCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg()
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = fullWidthWrap().apply { bottomMargin = dp(12) }
        }
        kpiCard.addView(label("مؤشرات الأداء", bold = true, size = 16f, color = Color.WHITE))
        kpiCard.addView(spacer(8))

        val row1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; layoutParams = fullWidthWrap() }
        row1.addView(statBox("متوسط الأعطال/شهر", "2.3", "#F44336"))
        row1.addView(statBox("نسبة توفر الأسطول", "94%", "#4CAF50"))
        kpiCard.addView(row1)
        kpiCard.addView(spacer(8))

        val row2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; layoutParams = fullWidthWrap() }
        row2.addView(statBox("عمر المركبة المتوسط", "4.2 سنة", "#7BBDE8"))
        row2.addView(statBox("توفير الصيانة الوقائية", "18%", "#4CAF50"))
        kpiCard.addView(row2)
        content.addView(kpiCard)

        // Strategies
        content.addView(sectionTitle("استراتيجيات فعالة"))
        content.addView(strategyCard(
            "الصيانة الوقائية الدورية",
            "تطبيق جداول صيانة منتظمة لكل مركبة بناءً على المسافة والوقت. يقلل من الأعطال المفاجئة بنسبة تصل إلى 40%.",
            "#4CAF50"
        ))
        content.addView(strategyCard(
            "مراقبة ضغط الإطارات",
            "فحص دوري لضغط الإطارات يطيل عمرها بنسبة 30% ويقلل استهلاك الوقود.",
            "#7BBDE8"
        ))
        content.addView(strategyCard(
            "تغيير الزيت في المواعيد",
            "الالتزام بمواعيد تغيير الزيت يحافظ على المحرك ويقلل تكاليف الإصلاح بنسبة 25%.",
            "#FFC107"
        ))
        content.addView(strategyCard(
            "تدريب السائقين",
            "برامج تدريبية للسائقين على القيادة الاقتصادية والتعامل الصحيح مع المركبات.",
            "#7BBDE8"
        ))
        content.addView(strategyCard(
            "إدارة قطع الغيار",
            "تخزين استراتيجي لقطع الغيار الأساسية يقلل وقت التوقف ويحسن جاهزية الأسطول.",
            "#4CAF50"
        ))

        // Top breakdowns
        content.addView(sectionTitle("الأعطال الأكثر شيوعاً"))
        content.addView(breakdownItem("تلف الإطارات", "35%", "#F44336"))
        content.addView(breakdownItem("مشاكل الفرامل", "22%", "#FFC107"))
        content.addView(breakdownItem("تسريب زيت المحرك", "18%", "#FFC107"))
        content.addView(breakdownItem("عطل البطارية", "15%", "#7BBDE8"))
        content.addView(breakdownItem("أعطال أخرى", "10%", "#7BBDE8"))
    }

    private fun strategyCard(title: String, desc: String, accent: String): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg()
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = fullWidthWrap().apply { bottomMargin = dp(10) }
        }
        // Accent bar
        card.addView(View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(4), dp(20))
            background = GradientDrawable().apply {
                cornerRadius = dp(4).toFloat()
                setColor(Color.parseColor(accent))
            }
        })
        card.addView(label(title, bold = true, size = 15f, color = Color.parseColor(accent), padTop = 8))
        card.addView(label(desc, size = 13f, color = Color.parseColor("#A0FFFFFF"), padTop = 6))
        return card
    }

    private fun breakdownItem(name: String, percentage: String, color: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = cardBg()
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = fullWidthWrap().apply { bottomMargin = dp(8) }
        }
        row.addView(label(name, size = 14f, color = Color.WHITE, weight = 1f))
        row.addView(label(percentage, bold = true, size = 14f, color = Color.parseColor(color)))
        return row
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
            addView(TextView(this@BreakdownReductionActivity).apply {
                this.text = value; setTextColor(Color.parseColor(color)); textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
            })
            addView(TextView(this@BreakdownReductionActivity).apply {
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
                      weight: Float = 0f, padTop: Int = 0, gravity: Int = android.view.Gravity.START): TextView {
        return TextView(this).apply {
            this.text = text; setTextColor(color); textSize = size
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(padTop), 0, 0); this.gravity = gravity
            if (weight > 0) layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
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
