package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.edham.logistics.core.network.RetrofitClient
import com.edham.logistics.data.remote.api.AnalyticsApi
import kotlinx.coroutines.launch

class SupervisorAnalyticsFragment : Fragment() {

    private lateinit var analyticsApi: AnalyticsApi
    private lateinit var progressBar: ProgressBar
    private lateinit var tvActiveShipments: TextView
    private lateinit var tvActiveDrivers: TextView
    private lateinit var tvOnTimeRate: TextView
    private lateinit var tvAvgResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi = RetrofitClient.createApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_supervisor_analytics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBar)
        tvActiveShipments = view.findViewById(R.id.tvActiveShipments)
        tvActiveDrivers = view.findViewById(R.id.tvActiveDrivers)
        tvOnTimeRate = view.findViewById(R.id.tvOnTimeRate)
        tvAvgResponse = view.findViewById(R.id.tvAvgResponse)
        loadAnalytics()
    }

    private fun loadAnalytics() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = analyticsApi.getRealTimeKPIs()
                if (response.isSuccessful && response.body() != null) {
                    val kpis = response.body()!!
                    tvActiveShipments.text = kpis.activeShipments.toString()
                    tvActiveDrivers.text = kpis.activeDrivers.toString()
                    tvOnTimeRate.text = "%.0f%%".format(kpis.onTimeDeliveryRate)
                    tvAvgResponse.text = "%.1f دقيقة".format(kpis.averageResponseTime)
                } else {
                    Toast.makeText(requireContext(), "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "فشل الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
