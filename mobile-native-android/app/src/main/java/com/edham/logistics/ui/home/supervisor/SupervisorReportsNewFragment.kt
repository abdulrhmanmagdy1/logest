package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupervisorReportsNewFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_reports_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        observeViewModel(view)
        viewModel.loadReportsData()
        viewModel.loadDashboardData()
        
        view.findViewById<View>(R.id.btnExportReports).setOnClickListener {
            Toast.makeText(context, "جاري تحضير التقارير الاستراتيجية للأسطول... 📑", Toast.LENGTH_LONG).show()
        }
    }

    private fun observeViewModel(view: View) {
        viewModel.revenueChart.observe(viewLifecycleOwner) { data ->
            setupRevenueLineChart(view.findViewById(R.id.revenueLineChart), data)
        }

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            setupUtilizationChart(view.findViewById(R.id.utilizationPieChart), stats)
        }
    }

    private fun setupRevenueLineChart(chart: LineChart, reportData: List<com.edham.logistics.core.network.api.ChartData>) {
        val entries = reportData.mapIndexed { index, data -> 
            Entry(index.toFloat(), data.value.toFloat()) 
        }

        val dataSet = LineDataSet(entries, "الإيرادات")
        dataSet.color = requireContext().getColor(R.color.ed_sky)
        dataSet.setCircleColor(requireContext().getColor(R.color.ed_sky))
        dataSet.lineWidth = 2f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = requireContext().getDrawable(R.drawable.bg_stat_icon_teal)
        
        chart.apply {
            this.data = LineData(dataSet)
            xAxis.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.textColor = requireContext().getColor(R.color.ed_text_muted_new)
            description.isEnabled = false
            legend.isEnabled = false
            animateX(1000)
            invalidate()
        }
    }

    private fun setupUtilizationChart(chart: PieChart, stats: com.edham.logistics.core.network.api.SupervisorStats) {
        val active = stats.in_transit.toFloat()
        val idle = (stats.available_vehicles - stats.in_transit).toFloat()
        
        val entries = listOf(
            PieEntry(active, "نشط"),
            PieEntry(idle, "خامل")
        )
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            requireContext().getColor(R.color.ed_emerald),
            requireContext().getColor(R.color.ed_navy_2)
        )
        dataSet.setDrawValues(false)
        
        chart.apply {
            this.data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            legend.textColor = requireContext().getColor(R.color.ed_white)
            setHoleColor(requireContext().getColor(R.color.ed_deep))
            setEntryLabelColor(android.graphics.Color.WHITE)
            animateY(1000)
            invalidate()
        }
    }
}
