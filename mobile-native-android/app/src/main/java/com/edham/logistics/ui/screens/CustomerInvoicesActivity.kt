package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import dagger.hilt.android.AndroidEntryPoint

import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.ui.home.customer.adapter.RecentShipmentAdapter

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

import androidx.activity.viewModels
import com.edham.logistics.ui.home.CustomerHomeViewModel

import com.edham.logistics.ui.home.customer.adapter.InvoiceAdapter
import android.content.Intent
import android.net.Uri

@AndroidEntryPoint
class CustomerInvoicesActivity : AppCompatActivity() {

    private val viewModel: CustomerHomeViewModel by viewModels()
    private lateinit var adapter: InvoiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_invoices)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        
        setupFilters()
        setupRecyclerView()
        setupSpendingChart()
        observeViewModel()
        
        viewModel.loadDashboardData()
    }

    private fun observeViewModel() {
        viewModel.invoices.observe(this) { invoices ->
            if (invoices.isNullOrEmpty()) {
                findViewById<View>(R.id.emptyStateInvoices).visibility = View.VISIBLE
                findViewById<View>(R.id.rvInvoices).visibility = View.GONE
            } else {
                findViewById<View>(R.id.emptyStateInvoices).visibility = View.GONE
                findViewById<View>(R.id.rvInvoices).visibility = View.VISIBLE
                adapter.updateData(invoices)
                
                // Calculate total spend
                val total = invoices.sumOf { it.amount }
                findViewById<TextView>(R.id.tvTotalSpend).text = String.format(java.util.Locale.getDefault(), "%,.0f", total)
            }
        }
    }

    private fun setupSpendingChart() {
        val chart = findViewById<BarChart>(R.id.miniChart)
        val data = listOf(
            Pair("سبت", 60f), Pair("أحد", 40f), Pair("إثن", 75f),
            Pair("ثلا", 30f), Pair("أرب", 85f), Pair("خمي", 55f), Pair("جمع", 90f)
        )

        val entries = data.mapIndexed { index, pair -> BarEntry(index.toFloat(), pair.second) }
        val dataSet = BarDataSet(entries, "الإنفاق اليومي")
        dataSet.color = getColor(R.color.prem_sky)
        dataSet.setDrawValues(false)

        chart.apply {
            this.data = BarData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first })
            xAxis.textColor = getColor(R.color.customer_text_muted)
            xAxis.setDrawGridLines(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun setupFilters() {
        val chips = listOf(
            findViewById<View>(R.id.chipAll),
            findViewById<View>(R.id.chipCold),
            findViewById<View>(R.id.chipDry),
            findViewById<View>(R.id.chipHeavy)
        )

        chips.forEach { chip ->
            chip.setOnClickListener {
                // Update UI state and filter list
                Toast.makeText(this, "تصفية: ${(chip as TextView).text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvInvoices)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = InvoiceAdapter(emptyList()) { invoice ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
            startActivity(browserIntent)
        }
        rv.adapter = adapter
    }
}
