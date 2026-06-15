package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.google.android.material.button.MaterialButton

/**
 * Multi-step Wizard for creating a new cargo request.
 * Controls the flow between steps 1-4.
 */
class CargoWizardActivity : AppCompatActivity() {

    private var currentStep = 1
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: View
    private lateinit var textStepIndicator: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cargo_wizard_production)

        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)
        textStepIndicator = findViewById(R.id.textStepIndicator)

        btnNext.setOnClickListener { nextStep() }
        btnBack.setOnClickListener { previousStep() }

        updateStep()
    }

    private fun nextStep() {
        if (currentStep < 4) {
            currentStep++
            updateStep()
        } else {
            // Finish and submit
            finish()
        }
    }

    private fun previousStep() {
        if (currentStep > 1) {
            currentStep--
            updateStep()
        } else {
            finish()
        }
    }

    private fun updateStep() {
        textStepIndicator.text = "خطوة $currentStep من 4"
        
        val fragment: Fragment = when (currentStep) {
            1 -> CargoTypeSelectionFragment()
            2 -> CargoSizeWeightFragment()
            3 -> CargoAddressFragment()
            else -> CargoReviewFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.wizard_container, fragment)
            .commit()
            
        btnNext.text = if (currentStep == 4) "تأكيد الطلب" else "التالي"
    }
}
