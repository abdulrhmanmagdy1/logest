package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edham.logistics.R

class CargoScheduleFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cargo_schedule, container, false)
        
        val scheduledOptions = view.findViewById<View>(R.id.scheduledOptions)
        view.findViewById<RadioGroup>(R.id.rgSchedule).setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbScheduled) {
                scheduledOptions.visibility = View.VISIBLE
                viewModel.isImmediate.value = false
            } else {
                scheduledOptions.visibility = View.GONE
                viewModel.isImmediate.value = true
            }
        }
        
        return view
    }
}
