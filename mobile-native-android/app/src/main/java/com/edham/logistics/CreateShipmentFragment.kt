package com.edham.logistics

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.core.network.RetrofitClient
import com.edham.logistics.data.local.entity.ShipmentEntity
import com.edham.logistics.data.remote.api.ShipmentApi
import com.edham.logistics.data.remote.api.VehicleApi
import kotlinx.coroutines.launch

class CreateShipmentFragment : Fragment() {
    private lateinit var vehicleAdapter: VehicleAdapter
    private var selectedVehicle: Vehicle? = null
    private lateinit var vehicleApi: VehicleApi
    private lateinit var shipmentApi: ShipmentApi
    private var vehiclesList = mutableListOf<Vehicle>()
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleApi = RetrofitClient.createApi()
        shipmentApi = RetrofitClient.createApi()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_shipment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val etPickup = view.findViewById<EditText>(R.id.et_pickup)
        val etDelivery = view.findViewById<EditText>(R.id.et_delivery)
        val etWeight = view.findViewById<EditText>(R.id.et_weight)
        val rvVehicles = view.findViewById<RecyclerView>(R.id.rv_vehicles)
        
        val tvDistance = view.findViewById<TextView>(R.id.tv_distance)
        val tvBasePrice = view.findViewById<TextView>(R.id.tv_base_price)
        val tvWeightCharge = view.findViewById<TextView>(R.id.tv_weight_charge)
        val tvTotal = view.findViewById<TextView>(R.id.tv_total)
        
        val btnCreate = view.findViewById<Button>(R.id.btn_create)
        progressBar = view.findViewById(R.id.progressBar)
        
        // Setup vehicle RecyclerView
        vehicleAdapter = VehicleAdapter(vehiclesList) { vehicle ->
            selectedVehicle = vehicle
            updatePrice(etWeight.text.toString(), tvDistance, tvBasePrice, tvWeightCharge, tvTotal)
        }
        
        rvVehicles.layoutManager = LinearLayoutManager(requireContext())
        rvVehicles.adapter = vehicleAdapter
        loadVehicles()
        
        // Weight text watcher
        etWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePrice(s.toString(), tvDistance, tvBasePrice, tvWeightCharge, tvTotal)
            }
        })
        
        // Create button
        btnCreate.setOnClickListener {
            if (validateInputs(etPickup, etDelivery, etWeight)) {
                createShipment()
            }
        }
    }
    
    private fun updatePrice(weight: String, tvDistance: TextView, tvBasePrice: TextView, tvWeightCharge: TextView, tvTotal: TextView) {
        if (selectedVehicle == null) return
        
        val weightKg = weight.toDoubleOrNull() ?: 0.0
        val distance = 950.0 // Simulated distance in km
        
        val basePrice = selectedVehicle!!.basePrice
        val weightCharge = weightKg * selectedVehicle!!.pricePerKg
        val total = basePrice + weightCharge
        
        tvDistance.text = "${distance.toInt()} km"
        tvBasePrice.text = "SAR $basePrice"
        tvWeightCharge.text = "SAR ${weightCharge.toInt()}"
        tvTotal.text = "SAR ${total.toInt()}"
    }
    
    private fun validateInputs(etPickup: EditText, etDelivery: EditText, etWeight: EditText): Boolean {
        if (etPickup.text.toString().isEmpty()) {
            etPickup.error = "Required"
            return false
        }
        
        if (etDelivery.text.toString().isEmpty()) {
            etDelivery.error = "Required"
            return false
        }
        
        if (etWeight.text.toString().isEmpty()) {
            etWeight.error = "Required"
            return false
        }
        
        if (selectedVehicle == null) {
            Toast.makeText(requireContext(), "Please select a vehicle", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun loadVehicles() {
        lifecycleScope.launch {
            try {
                val response = vehicleApi.getAllVehicles()
                if (response.isSuccessful && response.body() != null) {
                    vehiclesList.clear()
                    response.body()!!.forEach { dto ->
                        vehiclesList.add(Vehicle(
                            dto.id, dto.type, "${dto.capacity?.toInt() ?: 0} kg",
                            "", 200, 0.5 // basePrice/pricePerKg are mock calculations
                        ))
                    }
                    vehicleAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "فشل تحميل المركبات: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun createShipment() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val pickup = requireView().findViewById<EditText>(R.id.et_pickup).text.toString()
                val delivery = requireView().findViewById<EditText>(R.id.et_delivery).text.toString()
                val weight = requireView().findViewById<EditText>(R.id.et_weight).text.toString().toDoubleOrNull() ?: 0.0
                val now = System.currentTimeMillis().toString()
                val shipment = ShipmentEntity(
                    id = "0",
                    clientName = "",
                    clientPhone = "",
                    clientEmail = "",
                    pickupAddress = pickup,
                    deliveryAddress = delivery,
                    weight = weight,
                    dimensions = "",
                    cargoType = "general",
                    status = "pending",
                    price = 0.0,
                    vehicleId = selectedVehicle?.id,
                    trackingNumber = "EDH-$now",
                    createdAt = now,
                    updatedAt = now
                )
                shipmentApi.createShipment(shipment)
                Toast.makeText(requireContext(), "تم إنشاء الشحنة بنجاح!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "فشل الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
