package com.edham.logistics.ui.home.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession

/**
 * High-fidelity production dashboard for Customers.
 * Features Wallet, Stats, and Recent Shipments with the official Blue Theme.
 */
class CustomerDashboardProductionFragment : Fragment() {

    private lateinit var session: AuthSession

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_dashboard_production, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = AuthSession.get(requireContext())

        // Bind Greeting
        view.findViewById<TextView>(R.id.textUserName).text = session.displayName ?: "عميل إدهام"
        
        // Additional binding logic for stats and wallet would go here
    }
}
