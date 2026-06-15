package com.edham.logistics.ui.home.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.ui.home.CustomerHomeViewModel
import com.edham.logistics.ui.home.customer.adapter.RecentShipmentAdapter
import java.util.Locale
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * High-fidelity production dashboard for Customers.
 * Dynamically binds user session data and fetches real shipment stats.
 */
@AndroidEntryPoint
class CustomerDashboardProductionFragment : Fragment() {

    private lateinit var session: AuthSession
    private val viewModel: CustomerHomeViewModel by viewModels()
    private lateinit var adapter: RecentShipmentAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

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
        
        swipeRefresh = view.findViewById(R.id.swipeRefreshDashboard)
        swipeRefresh.setOnRefreshListener {
            viewModel.loadDashboardData()
        }

        setupRecyclerView(view)
        setupStaticBindings(view)
        observeViewModel(view)
        
        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewRecentShipments)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecentShipmentAdapter(emptyList()) { trip ->
            val intent = android.content.Intent(requireContext(), com.edham.logistics.ui.screens.TrackShipmentActivity::class.java)
            intent.putExtra("SHIPMENT_ID", trip.id)
            startActivity(intent)
        }
        rv.adapter = adapter
    }

    private fun setupStaticBindings(view: View) {
        view.findViewById<TextView>(R.id.textWelcome).text = "مرحباً بعودتك"
        view.findViewById<TextView>(R.id.textUserName).text = session.displayName ?: "عميل إدهام"

        view.findViewById<View>(R.id.btnNewCargo).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), com.edham.logistics.ui.home.customer.wizard.CargoWizardActivity::class.java))
        }

        view.findViewById<View>(R.id.btnViewAllShipments).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), com.edham.logistics.ui.screens.CustomerHistoryActivity::class.java))
        }
        
        view.findViewById<View>(R.id.btnRecharge).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), com.edham.logistics.ui.screens.CustomerRechargeActivity::class.java))
        }
    }

    private fun observeViewModel(view: View) {
        val emptyState = view.findViewById<View>(R.id.emptyStateDashboard)
        val recyclerView = view.findViewById<View>(R.id.recyclerViewRecentShipments)
        val walletText = view.findViewById<TextView>(R.id.textWalletBalance)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.walletBalance.observe(viewLifecycleOwner) { balance ->
            walletText.text = if (balance == 0.0) "0.00" else String.format(Locale.getDefault(), "%,.2f", balance)
        }

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            view.findViewById<TextView>(R.id.textPendingCount).text = stats.pending.toString()
            view.findViewById<TextView>(R.id.textActiveCount).text = stats.active.toString()
            view.findViewById<TextView>(R.id.textCompletedCount).text = stats.completed.toString()
        }

        viewModel.recentShipments.observe(viewLifecycleOwner) { shipments ->
            if (shipments.isNullOrEmpty()) {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateData(shipments)
            }
        }
    }
}
