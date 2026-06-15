package com.edham.logistics

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*

class AdminDashboardFragment : Fragment() {
    private lateinit var textTotalShipments: TextView
    private lateinit var textActiveDrivers: TextView
    private lateinit var textTotalRevenue: TextView
    private lateinit var textDelayedShipments: TextView
    private lateinit var textAverageDeliveryTime: TextView
    private lateinit var textCustomerSatisfaction: TextView
    private lateinit var textFleetUtilization: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var contentLayout: ScrollView
    private lateinit var layoutError: LinearLayout
    private lateinit var textError: TextView
    private lateinit var buttonRetry: com.google.android.material.button.MaterialButton
    private lateinit var buttonRefresh: com.google.android.material.button.MaterialButton
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            refreshData()
            handler.postDelayed(this, 30000) // Refresh every 30 seconds
        }
    }
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        textTotalShipments = view.findViewById(R.id.textTotalShipments)
        textActiveDrivers = view.findViewById(R.id.textActiveDrivers)
        textTotalRevenue = view.findViewById(R.id.textTotalRevenue)
        textDelayedShipments = view.findViewById(R.id.textDelayedShipments)
        textAverageDeliveryTime = view.findViewById(R.id.textAverageDeliveryTime)
        textCustomerSatisfaction = view.findViewById(R.id.textCustomerSatisfaction)
        textFleetUtilization = view.findViewById(R.id.textFleetUtilization)
        progressBar = view.findViewById(R.id.progressBar)
        contentLayout = view.findViewById(R.id.contentLayout)
        layoutError = view.findViewById(R.id.layoutError)
        textError = view.findViewById(R.id.textError)
        buttonRetry = view.findViewById(R.id.buttonRetry)
        buttonRefresh = view.findViewById(R.id.buttonRefresh)

        // Setup refresh button
        buttonRefresh.setOnClickListener {
            refreshData()
        }

        // Setup retry button
        buttonRetry.setOnClickListener {
            refreshData()
        }

        // Load initial data
        loadData()

        // Start auto refresh
        handler.post(refreshRunnable)
    }

    private fun loadData() {
        // Set mock data
        textTotalShipments.text = "1,234"
        textActiveDrivers.text = "56"
        textTotalRevenue.text = "45,678 ر.س"
        textDelayedShipments.text = "12"
        textAverageDeliveryTime.text = "45 دقيقة"
        textCustomerSatisfaction.text = "92%"
        textFleetUtilization.text = "78%"
    }

    private fun refreshData() {
        // Refresh data logic
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(refreshRunnable)
        scope.cancel()
    }
}
