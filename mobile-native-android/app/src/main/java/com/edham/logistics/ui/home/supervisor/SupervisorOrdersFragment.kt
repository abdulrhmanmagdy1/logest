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
import com.edham.logistics.data.local.entity.ShipmentEntity
import com.edham.logistics.data.remote.api.ShipmentApi
import kotlinx.coroutines.launch

class SupervisorOrdersFragment : Fragment() {

    private lateinit var shipmentApi: ShipmentApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private var shipmentsList = mutableListOf<ShipmentEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shipmentApi = RetrofitClient.createApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_supervisor_orders, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvOrders)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = OrdersAdapter(shipmentsList) { order ->
            Toast.makeText(requireContext(), "تم اختيار: ${order.id}", Toast.LENGTH_SHORT).show()
        }

        loadOrders()
    }

    private fun loadOrders() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val shipments = shipmentApi.getShipments()
                shipmentsList.clear()
                shipmentsList.addAll(shipments)
                recyclerView.adapter?.notifyDataSetChanged()
                tvEmpty.visibility = if (shipmentsList.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "فشل الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
                tvEmpty.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    inner class OrdersAdapter(
        private val items: List<ShipmentEntity>,
        private val onItemClick: (ShipmentEntity) -> Unit
    ) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: ShipmentEntity) {
                itemView.findViewById<TextView>(R.id.tvOrderId).text = item.id
                itemView.findViewById<TextView>(R.id.tvOrderStatus).text = item.status ?: "جديد"
                itemView.findViewById<TextView>(R.id.tvOrderRoute).text = "${item.pickupAddress} \u2190 ${item.deliveryAddress}"
                itemView.setOnClickListener { onItemClick(item) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
        override fun getItemCount(): Int = items.size
    }
}
