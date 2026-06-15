package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.google.android.material.slider.Slider
import java.util.Locale

import androidx.fragment.app.activityViewModels

class CargoSizeWeightFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cargo_size_weight, container, false)
        
        val textWeight = view.findViewById<TextView>(R.id.textWeightValue)
        val textPrice = view.findViewById<TextView>(R.id.textEstimatePrice)
        val slider = view.findViewById<Slider>(R.id.weightSlider)

        slider.addOnChangeListener { _, value, _ ->
            val w = value.toInt()
            if (w <= 0) {
                slider.value = 1f
                return@addOnChangeListener
            }
            textWeight.text = String.format(Locale.getDefault(), "%d كجم", w)
            viewModel.weight.value = w.toString()
            viewModel.calculateEstimate()
        }

        viewModel.estimatedPrice.observe(viewLifecycleOwner) { price ->
            textPrice.text = String.format(Locale.getDefault(), "%.2f ريال", price)
        }

        // Logic for Size Buttons
        val sizes = listOf(
            view.findViewById<TextView>(R.id.size_small),
            view.findViewById<TextView>(R.id.size_medium),
            view.findViewById<TextView>(R.id.size_large)
        )

        sizes.forEach { btn ->
            btn?.setOnClickListener {
                sizes.forEach { it?.alpha = 0.5f }
                btn.alpha = 1.0f
            }
        }

        return view
    }
}
