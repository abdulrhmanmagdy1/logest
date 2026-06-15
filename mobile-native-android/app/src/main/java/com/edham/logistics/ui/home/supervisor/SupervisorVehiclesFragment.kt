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
import com.edham.logistics.data.remote.api.VehicleApi
import com.edham.logistics.data.remote.dto.VehicleDto
import kotlinx.coroutines.launch

class SupervisorVehiclesFragment : Fragment() {

    private lateinit var vehicleApi: VehicleApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private var vehiclesList = mutableListOf<VehicleDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleApi = RetrofitClient.createApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_supervisor_vehicles, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvVehicles)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = VehiclesAdapter(vehiclesList) { vehicle ->
            Toast.makeText(requireContext(), "تم اختيار: ${vehicle.plateNumber}", Toast.LENGTH_SHORT).show()
        }
        loadVehicles()
    }

    private fun loadVehicles() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val response = vehicleApi.getAllVehicles()
                if (response.isSuccessful && response.body() != null) {
                    vehiclesList.clear()
                    vehiclesList.addAll(response.body()!!)
                    recyclerView.adapter?.notifyDataSetChanged()
                    tvEmpty.visibility = if (vehiclesList.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(requireContext(), "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show()
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

    inner class VehiclesAdapter(
        private val items: List<VehicleDto>,
        private val onItemClick: (VehicleDto) -> Unit
    ) : RecyclerView.Adapter<VehiclesAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: VehicleDto) {
                itemView.findViewById<TextView>(R.id.tv_vehicle_name).text = item.plateNumber
                itemView.findViewById<TextView>(R.id.tv_capacity).text = item.type
                itemView.findViewById<TextView>(R.id.tv_dimensions).text = item.status
                itemView.findViewById<TextView>(R.id.tv_price).text = item.driverName ?: "بدون سائق"
                itemView.setOnClickListener { onItemClick(item) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vehicle, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
        override fun getItemCount(): Int = items.size
    }
}
