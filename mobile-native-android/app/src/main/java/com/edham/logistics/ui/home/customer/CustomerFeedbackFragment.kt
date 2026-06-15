package com.edham.logistics.ui.home.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerFeedbackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<View>(R.id.btnSubmitFeedback).setOnClickListener {
            // In a real app, send to API
            Toast.makeText(context, "شكراً لك! تم استلام تقييمك بنجاح ❤️", Toast.LENGTH_LONG).show()
            activity?.onBackPressed()
        }
    }

    companion object {
        fun newInstance(shipmentId: String) = CustomerFeedbackFragment().apply {
            arguments = Bundle().apply { putString("SHIPMENT_ID", shipmentId) }
        }
    }
}
