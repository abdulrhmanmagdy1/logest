package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.edham.logistics.feature.driver.presentation.viewmodels.DriverDashboardViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDashboardNewFragment : Fragment() {

    private val viewModel: DriverDashboardViewModel by viewModels()
    private lateinit var tvTodayEarnings: TextView
    private lateinit var tvLiveTemp: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_dashboard_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tvTodayEarnings = view.findViewById(R.id.tvTodayEarnings)
        
        setupTabs(view)
        setupListeners(view)
        
        // Initial data load would go here via viewModel
    }

    private fun setupTabs(view: View) {
        val tabs = view.findViewById<TabLayout>(R.id.cockpitTabs)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position) {
                    0 -> showHomeSection()
                    1 -> showMissionSection()
                    2 -> showCockpitSection()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.btnStartMission).setOnClickListener {
            Toast.makeText(context, "إشعال المحرك... تم بدء الرحلة بنجاح! 🚛", Toast.LENGTH_LONG).show()
        }
    }

    private fun showHomeSection() {
        // Logic to swap UI or fragments
    }

    private fun showMissionSection() {
        // Navigate to mission or update layout
    }

    private fun showCockpitSection() {
        // Navigate to cockpit or update layout
    }
}
