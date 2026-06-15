package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverMissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_mission_tactical)
        
        setupEarningsChart()
    }

    private fun setupEarningsChart() {
        val chart = findViewById<LineChart>(R.id.earningsRadar) ?: return
        
        val entries = listOf(
            Entry(0f, 100f), Entry(1f, 250f), Entry(2f, 180f),
            Entry(3f, 400f), Entry(4f, 320f), Entry(5f, 500f)
        )

        val dataSet = LineDataSet(entries, "الأرباح").apply {
            color = getColor(R.id.vault_emerald)
            setDrawFilled(true)
            fillDrawable = getDrawable(R.drawable.circle_glow)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 3f
            setDrawCircles(false)
        }

        chart.data = LineData(dataSet)
        chart.invalidate()
    }
}
