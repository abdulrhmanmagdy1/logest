package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edham.logistics.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider

class CargoTypeSelectionFragment : Fragment() {

    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cargo_type_selection, container, false)
        
        setupCargoTypes(view)
        setupWeight(view)
        
        return view
    }

    private fun setupCargoTypes(view: View) {
        val coldCard = view.findViewById<MaterialCardView>(R.id.type_refrigerated)
        val frozenCard = view.findViewById<MaterialCardView>(R.id.type_frozen)
        val dryCard = view.findViewById<MaterialCardView>(R.id.type_dry)
        val bulkCard = view.findViewById<MaterialCardView>(R.id.type_bulk)
        val warehouseCard = view.findViewById<MaterialCardView>(R.id.type_warehouse)

        val all = listOf(coldCard, frozenCard, dryCard, bulkCard, warehouseCard)

        coldCard.setOnClickListener { selectCargo("REFRIGERATED", coldCard, all - coldCard) }
        frozenCard.setOnClickListener { selectCargo("FROZEN", frozenCard, all - frozenCard) }
        dryCard.setOnClickListener { selectCargo("DRY", dryCard, all - dryCard) }
        bulkCard.setOnClickListener { selectCargo("BULK", bulkCard, all - bulkCard) }
        warehouseCard.setOnClickListener { selectCargo("WAREHOUSE", warehouseCard, all - warehouseCard) }
    }

    private fun setupWeight(view: View) {
        val w5 = view.findViewById<MaterialCardView>(R.id.weight_5)
        val w10 = view.findViewById<MaterialCardView>(R.id.weight_10)
        val slider = view.findViewById<Slider>(R.id.weightSlider)
        val display = view.findViewById<TextView>(R.id.textWeightDisplay)

        w5.setOnClickListener { 
            updateWeight("5", w5, listOf(w10), slider, display)
        }
        w10.setOnClickListener { 
            updateWeight("10", w10, listOf(w5), slider, display)
        }

        slider.addOnChangeListener { _, value, _ ->
            val w = value.toInt().toString()
            viewModel.weight.value = w
            display.text = "الوزن الحالي: $w طن"
            // Deselect fixed cards if manual slider is moved
            listOf(w5, w10).forEach { 
                it.strokeWidth = 2
                it.strokeColor = resources.getColor(R.color.customer_blue_border, null)
            }
        }
    }

    private fun selectCargo(type: String, selected: MaterialCardView, others: List<MaterialCardView>) {
        viewModel.cargoType.value = type
        selected.strokeWidth = 4
        selected.strokeColor = resources.getColor(R.color.prem_sky, null)
        others.forEach { 
            it.strokeWidth = 2
            it.strokeColor = resources.getColor(R.color.customer_blue_border, null)
        }
    }

    private fun updateWeight(w: String, selected: MaterialCardView, others: List<MaterialCardView>, slider: Slider, display: TextView) {
        viewModel.weight.value = w
        slider.value = w.toFloat()
        display.text = "الوزن الحالي: $w طن"
        
        selected.strokeWidth = 4
        selected.strokeColor = resources.getColor(R.color.prem_sky, null)
        others.forEach { 
            it.strokeWidth = 2
            it.strokeColor = resources.getColor(R.color.customer_blue_border, null)
        }
    }
}
