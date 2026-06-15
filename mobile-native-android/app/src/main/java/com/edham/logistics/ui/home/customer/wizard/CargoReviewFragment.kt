package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edham.logistics.R

class CargoReviewFragment : Fragment() {

    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cargo_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Real data binding from ViewModel
        view.findViewById<TextView>(R.id.review_pickup).text = viewModel.pickupAddress.value ?: "غير محدد"
        view.findViewById<TextView>(R.id.review_delivery).text = viewModel.deliveryAddress.value ?: "غير محدد"
        view.findViewById<TextView>(R.id.review_weight).text = getString(R.string.weight_kg, viewModel.weight.value ?: "0")
        
        val typeText = when(viewModel.cargoType.value) {
            "COLD" -> "شحن مبرد ❄️"
            "DRY" -> "شحن جاف 📦"
            "HEAVY" -> "حمولات ثقيلة 🚛"
            else -> "شحن عام"
        }
        view.findViewById<TextView>(R.id.review_type).text = typeText
        
        // Remove fake ID and show actual calculation
        view.findViewById<TextView>(R.id.review_order_id).text = "#NEW_REQUEST"
        
        viewModel.estimatedPrice.observe(viewLifecycleOwner) { price ->
            view.findViewById<TextView>(R.id.review_total_price).text = 
                String.format(java.util.Locale.getDefault(), "%,.0f ريال", price)
        }
    }
}
