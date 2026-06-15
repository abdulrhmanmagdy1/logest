package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FleetRiskMatrixFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supervisor_risk_matrix, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRiskHeatmap(view.findViewById(R.id.riskHeatmap))
    }

    private fun setupRiskHeatmap(chart: BubbleChart) {
        val entries = listOf(
            BubbleEntry(1f, 10f, 10f), // (Speed, Stress, ImpactSize)
            BubbleEntry(2f, 15f, 5f),
            BubbleEntry(3f, 25f, 20f), // High risk point
            BubbleEntry(4f, 12f, 8f)
        )

        val dataSet = BubbleDataSet(entries, "نقاط التركز")
        dataSet.colors = listOf(
            requireContext().getColor(R.color.ed_sky),
            requireContext().getColor(R.color.ed_emerald),
            requireContext().getColor(R.color.ed_rust),
            requireContext().getColor(R.color.ed_sky)
        )
        dataSet.setDrawValues(false)
        
        chart.apply {
            this.data = BubbleData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setTouchEnabled(false)
            animateXY(1000, 1000)
            invalidate()
        }
    }
}
