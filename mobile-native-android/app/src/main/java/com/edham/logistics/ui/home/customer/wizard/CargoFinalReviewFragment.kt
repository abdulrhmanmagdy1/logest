package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edham.logistics.R

class CargoFinalReviewFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cargo_final_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<TextView>(R.id.tvPickupSummary).text = viewModel.pickupAddress.value ?: "---"
        view.findViewById<TextView>(R.id.tvDeliverySummary).text = viewModel.deliveryAddress.value ?: "---"
        view.findViewById<TextView>(R.id.tvCargoSummary).text = viewModel.cargoType.value ?: "---"
        view.findViewById<TextView>(R.id.tvWeightSummary).text = getString(R.string.weight_kg, viewModel.weight.value ?: "0")
        view.findViewById<TextView>(R.id.tvVehicleSummary).text = viewModel.vehicleType.value ?: "---"
        view.findViewById<TextView>(R.id.tvScheduleSummary).text = if (viewModel.isImmediate.value == true) "فوري" else "مجدول"
    }
}
