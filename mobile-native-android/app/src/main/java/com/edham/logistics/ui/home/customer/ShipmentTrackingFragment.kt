package com.edham.logistics.ui.home.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.edham.logistics.ui.home.CustomerHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShipmentTrackingFragment : Fragment() {

    private val viewModel: CustomerHomeViewModel by viewModels()
    private var shipmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shipmentId = arguments?.getString("LOAD_ID")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shipment_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressed()
        }

        observeViewModel(view)
        
        shipmentId?.let { viewModel.loadTrackingInfo(it) }
    }

    private fun observeViewModel(view: View) {
        viewModel.trackingData.observe(viewLifecycleOwner) { load ->
            if (load != null) {
                view.findViewById<TextView>(R.id.tvTrackingNumber).text = load.id
                view.findViewById<TextView>(R.id.tvStatus).text = mapStatusToArabic(load.status)
                view.findViewById<TextView>(R.id.tvEstimatedDelivery).text = load.date
                view.findViewById<TextView>(R.id.tvDriverName).text = load.driverName ?: "جاري البحث عن كابتن..."
                view.findViewById<TextView>(R.id.tvVehicleInfo).text = load.truckNumber ?: "---"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun mapStatusToArabic(status: String): String = when(status.lowercase()) {
        "pending" -> "قيد الانتظار"
        "assigned" -> "تم تعيين الكابتن"
        "picked_up" -> "تم التحميل"
        "on_the_way" -> "في الطريق إليك"
        "delivered" -> "تم التسليم بنجاح"
        else -> status
    }

    companion object {
        fun newInstance(id: String) = ShipmentTrackingFragment().apply {
            arguments = Bundle().apply { putString("LOAD_ID", id) }
        }
    }
}
