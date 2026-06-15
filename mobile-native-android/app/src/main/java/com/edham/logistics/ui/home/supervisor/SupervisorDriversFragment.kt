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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.core.network.RetrofitClient
import com.edham.logistics.data.remote.api.AnalyticsApi
import com.edham.logistics.data.remote.api.DriverAnalyticsResponse
import kotlinx.coroutines.launch

class SupervisorDriversFragment : Fragment() {

    private lateinit var analyticsApi: AnalyticsApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private val driversList = mutableListOf<DriverItem>()

    data class DriverItem(val name: String, val rating: String, val deliveries: String, val status: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi = RetrofitClient.createApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_supervisor_drivers, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvDrivers)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = DriversAdapter(driversList) { driver ->
            Toast.makeText(requireContext(), "تم اختيار: ${driver.name}", Toast.LENGTH_SHORT).show()
        }
        loadDrivers()
    }

    private fun loadDrivers() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val response = analyticsApi.getDriverAnalytics("2026-01-01", "2026-12-31")
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!
                    driversList.clear()
                    driversList.add(DriverItem("مجموع السائقين", stats.totalDrivers.toString(), "", ""))
                    driversList.add(DriverItem("نشطون", stats.activeDrivers.toString(), "", ""))
                    driversList.add(DriverItem("متوسط التقييم", "%.1f".format(stats.averageRating), "", ""))
                    driversList.add(DriverItem("متوسط التوصيلات", "%.0f".format(stats.averageDeliveriesPerDriver), "", ""))
                    recyclerView.adapter?.notifyDataSetChanged()
                    tvEmpty.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "فشل الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
                tvEmpty.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    inner class DriversAdapter(
        private val items: List<DriverItem>,
        private val onItemClick: (DriverItem) -> Unit
    ) : RecyclerView.Adapter<DriversAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: DriverItem) {
                itemView.findViewById<TextView>(R.id.tv_driver_name).text = item.name
                itemView.findViewById<TextView>(R.id.tv_driver_rating).text = item.rating
                itemView.findViewById<TextView>(R.id.tv_driver_trips).text = item.deliveries
                itemView.findViewById<TextView>(R.id.tv_driver_status).text = item.status
                itemView.setOnClickListener { onItemClick(item) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_driver, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
        override fun getItemCount(): Int = items.size
    }
}
