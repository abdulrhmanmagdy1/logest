package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.google.android.material.button.MaterialButton

import androidx.activity.viewModels
import com.edham.logistics.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Multi-step Wizard for creating a new cargo request.
 * Controls the flow between steps 1-4.
 */
@AndroidEntryPoint
class CargoWizardActivity : BaseActivity() {

    private val viewModel: CargoWizardViewModel by viewModels()
    private var currentStep = 1
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: View
    private lateinit var textStepIndicator: TextView
    private lateinit var wizardFooter: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cargo_wizard_production)

        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)
        textStepIndicator = findViewById(R.id.textStepIndicator)
        wizardFooter = findViewById(R.id.bottom_bar)

        btnNext.setOnClickListener { nextStep() }
        btnBack.setOnClickListener { previousStep() }
        
        // Navigation back via header
        findViewById<View>(R.id.btnBack)?.setOnClickListener { 
            previousStep() 
        }

        updateStep()
    }

    private fun nextStep() {
        if (!validateCurrentStep()) return

        val maxSteps = 11
        if (currentStep < maxSteps) {
            if (currentStep == 10) {
                viewModel.submitRequest()
            }
            currentStep++
            updateStep()
        } else {
            finish()
        }
    }

    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            1 -> {
                if (viewModel.cargoType.value.isNullOrEmpty()) {
                    showError("يرجى اختيار نوع الحمولة")
                    false
                } else true
            }
            2 -> {
                if (viewModel.weight.value.isNullOrEmpty()) {
                    showError("يرجى تحديد الوزن")
                    false
                } else true
            }
            4 -> {
                if (viewModel.pickupAddress.value.isNullOrEmpty() || viewModel.deliveryAddress.value.isNullOrEmpty()) {
                    showError("يرجى إدخال عناوين الاستلام والتسليم")
                    false
                } else true
            }
            else -> true
        }
    }

    private fun showError(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun previousStep() {
        if (currentStep > 1) {
            currentStep--
            updateStep()
        } else {
            // Exit Wizard back to Home
            finish()
        }
    }

    private fun updateStep() {
        val totalSteps = 11
        textStepIndicator.text = "خطوة $currentStep من $totalSteps"
        
        val fragment: Fragment = when (currentStep) {
            1 -> CargoTypeSelectionFragment()
            2 -> CargoSizeWeightFragment()
            3 -> CargoDetailsFragment()
            4 -> CargoAddressFragment()
            5 -> CargoPickupFragment()
            6 -> CargoDeliveryFragment()
            7 -> CargoVehicleFragment()
            8 -> CargoMediaFragment()
            9 -> CargoScheduleFragment()
            10 -> CargoFinalReviewFragment()
            else -> CargoConfirmationFragment()
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.wizard_container, fragment)
            .commit()
            
        // Labels
        btnNext.text = when (currentStep) {
            10 -> "إرسال الطلب"
            11 -> "إنهاء"
            else -> "التالي"
        }

        if (currentStep == 11) { // Success screen
            wizardFooter.visibility = View.GONE
            textStepIndicator.visibility = View.GONE
        } else {
            wizardFooter.visibility = View.VISIBLE
            textStepIndicator.visibility = View.VISIBLE
        }
    }
}
