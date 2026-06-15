package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edham.logistics.R

class CargoDetailsFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cargo_details_new, container, false)
        
        view.findViewById<EditText>(R.id.etType).doAfterTextChanged {
            viewModel.cargoType.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etDescription).doAfterTextChanged {
            viewModel.cargoDescription.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etWeight).doAfterTextChanged {
            viewModel.weight.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etPieceCount).doAfterTextChanged {
            viewModel.pieceCount.value = it.toString()
        }
        
        view.findViewById<EditText>(R.id.etDimensions).doAfterTextChanged {
            viewModel.dimensions.value = it.toString()
        }
        
        view.findViewById<CheckBox>(R.id.cbFragile).setOnCheckedChangeListener { _, isChecked ->
            viewModel.isFragile.value = isChecked
        }
        
        view.findViewById<CheckBox>(R.id.cbCooling).setOnCheckedChangeListener { _, isChecked ->
            viewModel.needsCooling.value = isChecked
        }
        
        return view
    }
}
