package com.edham.logistics.ui.home.customer.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edham.logistics.R

class CargoConfirmationFragment : Fragment() {
    private val viewModel: CargoWizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cargo_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.createdShipmentId.observe(viewLifecycleOwner) { id ->
            view.findViewById<TextView>(R.id.tvConfirmationId).text = getString(R.string.tracking_number_label, id)
        }
        
        // Hide fake ETA values or set them to "Calculating..."
        view.findViewById<TextView>(R.id.tvEtaValue).text = "جاري الحساب..."
        
        view.findViewById<View>(R.id.btnHome).setOnClickListener {
            activity?.finish()
        }
        
        view.findViewById<View>(R.id.btnTrack).setOnClickListener {
            val id = viewModel.createdShipmentId.value
            if (id != null) {
                val intent = android.content.Intent(requireContext(), com.edham.logistics.ui.screens.TrackShipmentActivity::class.java)
                intent.putExtra("SHIPMENT_ID", id)
                startActivity(intent)
            }
            activity?.finish()
        }
    }
}
