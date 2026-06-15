package com.edham.logistics.ui.home.driver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.edham.logistics.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.feature.driver.presentation.viewmodels.DriverDashboardViewModel
import com.edham.logistics.feature.driver.data.models.DriverStats
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverMissionActivity : AppCompatActivity() {

    private val viewModel: DriverDashboardViewModel by viewModels()
    private lateinit var missionAdapter: com.edham.logistics.ui.home.driver.adapter.TacticalMissionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_mission_tactical)
        
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvTacticalMissions)
        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        missionAdapter = com.edham.logistics.ui.home.driver.adapter.TacticalMissionAdapter(emptyList())
        rv.adapter = missionAdapter
    }

    private fun setupListeners() {
        findViewById<android.view.View>(R.id.btnBack)?.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        val session = com.edham.logistics.app.AuthSession.get(this)
        session.userId?.let { viewModel.loadData(it) }

        viewModel.stats.observe(this) { stats ->
            // Update chart or telemetry with real stats
            renderTelemetryChart(stats)
        }
    }

    private fun renderTelemetryChart(stats: DriverStats) {
        val chart = findViewById<LineChart>(R.id.earningsRadar) ?: return
        
        // Simulating trend based on actual daily earnings
        val base = stats.todayEarnings / 5
        val entries = listOf(
            Entry(0f, base.toFloat() * 0.8f), 
            Entry(1f, base.toFloat() * 1.2f), 
            Entry(2f, base.toFloat() * 0.9f),
            Entry(3f, stats.todayEarnings.toFloat())
        )

        val dataSet = LineDataSet(entries, "الأرباح").apply {
            color = ContextCompat.getColor(this@DriverMissionActivity, R.color.vault_emerald)
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(this@DriverMissionActivity, R.drawable.circle_glow)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 3f
            setDrawCircles(false)
            setDrawValues(false)
        }

        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.axisLeft.textColor = ContextCompat.getColor(this, R.color.customer_text_muted)
        chart.axisRight.isEnabled = false
        chart.invalidate()
    }
}
