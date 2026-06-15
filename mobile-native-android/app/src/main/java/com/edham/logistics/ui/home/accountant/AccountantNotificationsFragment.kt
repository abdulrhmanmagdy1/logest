package com.edham.logistics.ui.home.accountant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.supervisor.SupervisorViewModel
import com.edham.logistics.ui.home.supervisor.adapter.SmartAlertAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountantNotificationsFragment : Fragment() {

    private val viewModel: SupervisorViewModel by viewModels() 

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accountant_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvNotifications)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SmartAlertAdapter(emptyList()) { 
            // Handle alert action
        }
        rv.adapter = adapter

        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            adapter.updateData(alerts)
        }
        
        viewModel.loadDashboardData()
    }
}
