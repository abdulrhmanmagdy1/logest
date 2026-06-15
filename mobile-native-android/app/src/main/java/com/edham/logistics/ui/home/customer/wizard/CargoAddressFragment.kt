package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edham.logistics.R

class CargoAddressFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()

    private val pickLocation = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val lat = result.data?.getDoubleExtra("LAT", 0.0) ?: 0.0
            val lng = result.data?.getDoubleExtra("LNG", 0.0) ?: 0.0
            val isPickup = result.data?.getBooleanExtra("IS_PICKUP", true) ?: true
            
            if (isPickup) {
                viewModel.pickupLat.value = lat
                viewModel.pickupLng.value = lng
                view?.findViewById<EditText>(R.id.etPickup)?.setText("موقع الاستلام ($lat, $lng)")
            } else {
                viewModel.deliveryLat.value = lat
                viewModel.deliveryLng.value = lng
                view?.findViewById<EditText>(R.id.etDelivery)?.setText("موقع التسليم ($lat, $lng)")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cargo_address, container, false)
        
        view.findViewById<EditText>(R.id.etPickup).doAfterTextChanged {
            viewModel.pickupAddress.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etDelivery).doAfterTextChanged {
            viewModel.deliveryAddress.value = it.toString()
        }
        
        view.findViewById<View>(R.id.btnPickPickupFromMap).setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.edham.logistics.ui.screens.MapPickerActivity::class.java)
            intent.putExtra("IS_PICKUP", true)
            pickLocation.launch(intent)
        }

        view.findViewById<View>(R.id.btnPickDeliveryFromMap).setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.edham.logistics.ui.screens.MapPickerActivity::class.java)
            intent.putExtra("IS_PICKUP", false)
            pickLocation.launch(intent)
        }
        
        return view
    }
}
