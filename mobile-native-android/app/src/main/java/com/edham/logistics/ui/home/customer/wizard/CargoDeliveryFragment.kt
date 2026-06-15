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

class CargoDeliveryFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cargo_delivery, container, false)
        
        view.findViewById<EditText>(R.id.etCity).doAfterTextChanged {
            viewModel.deliveryCity.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etAddress).doAfterTextChanged {
            viewModel.deliveryAddress.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etReceiverName).doAfterTextChanged {
            viewModel.receiverName.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etReceiverPhone).doAfterTextChanged {
            viewModel.receiverPhone.value = it.toString()
        }
        
        return view
    }
}
