package com.edham.logistics.ui.home.accountant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountantReportsFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accountant_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        observeViewModel(view)
        viewModel.loadDashboardData()
    }

    private fun observeViewModel(view: View) {
        viewModel.dashboardStats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.tvGrossRev).text = "${String.format("%,.0f", stats.liquidity + stats.outstanding_debts)} ج"
            view.findViewById<TextView>(R.id.tvFuelExp).text = "${String.format("%,.0f", stats.monthly_expenses * 0.4)} ج"
            view.findViewById<TextView>(R.id.tvSalaryExp).text = "${String.format("%,.0f", stats.monthly_expenses * 0.3)} ج"
            view.findViewById<TextView>(R.id.tvMaintExp).text = "${String.format("%,.0f", stats.monthly_expenses * 0.3)} ج"
            view.findViewById<TextView>(R.id.tvNetProfitDetail).text = "${String.format("%,.0f", stats.net_profit)} ج"
            
            setupChart(view, stats.profit_history)
        }
    }

    private fun setupChart(view: View, chartData: List<com.edham.logistics.core.network.api.ChartData>) {
        val chart = view.findViewById<LineChart>(R.id.profitLineChart)
        val entries = chartData.mapIndexed { index, item -> Entry(index.toFloat(), item.value.toFloat()) }
        
        val dataSet = LineDataSet(entries, "صافي الربح")
        dataSet.color = requireContext().getColor(R.color.acc_sky)
        dataSet.valueTextColor = requireContext().getColor(R.color.acc_text_muted)
        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(true)
        dataSet.setCircleColor(requireContext().getColor(R.color.acc_sky))
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        
        chart.apply {
            this.data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.textColor = requireContext().getColor(R.color.acc_text_muted)
            setTouchEnabled(true)
            animateX(1000)
            invalidate()
        }
    }
}
