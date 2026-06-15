package com.edham.logistics

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomerDashboardFragment : Fragment() {

    // Views
    private lateinit var tvTotalShipments: TextView
    private lateinit var tvActiveShipments: TextView
    private lateinit var tvCompletedShipments: TextView
    private lateinit var tvPendingShipments: TextView
    private lateinit var tvInProgressShipments: TextView
    private lateinit var tvDelayedShipments: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvLastLogin: TextView
    private lateinit var tvSeeAll: TextView
    private lateinit var cardCreateShipment: CardView
    private lateinit var progressBar: View
    
    // Enhanced Welcome Section
    private lateinit var ivProfilePicture: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivQRCode: ImageView
    
    // Quick Actions
    private lateinit var actionTrackShipment: View
    private lateinit var actionGetQuote: View
    private lateinit var actionPayment: View
    private lateinit var actionSupport: View
    
    // Shortcuts
    private lateinit var ivLogo: ImageView
    private lateinit var btnTrackShipment: View
    private lateinit var btnCreateShipment: View
    private lateinit var btnNotifications: View
    private lateinit var notificationBadge: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_customer_dashboard_with_shortcuts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupCounters()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        tvTotalShipments = view.findViewById(R.id.tvTotalShipments)
        tvActiveShipments = view.findViewById(R.id.tvActiveShipments)
        tvCompletedShipments = view.findViewById(R.id.tvCompletedShipments)
        tvPendingShipments = view.findViewById(R.id.tvPendingShipments)
        tvInProgressShipments = view.findViewById(R.id.tvInProgressShipments)
        tvDelayedShipments = view.findViewById(R.id.tvDelayedShipments)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvLastLogin = view.findViewById(R.id.tvLastLogin)
        tvSeeAll = view.findViewById(R.id.tvSeeAll)
        cardCreateShipment = view.findViewById(R.id.cardCreateShipment)
        progressBar = view.findViewById(R.id.progressBar)
        
        // Enhanced Welcome Section
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture)
        etSearch = view.findViewById(R.id.etSearch)
        ivQRCode = view.findViewById(R.id.ivQRCode)

        // Quick Actions
        actionTrackShipment = view.findViewById(R.id.actionTrackShipment)
        actionGetQuote = view.findViewById(R.id.actionGetQuote)
        actionPayment = view.findViewById(R.id.actionPayment)
        actionSupport = view.findViewById(R.id.actionSupport)

        // Shortcuts
        ivLogo = view.findViewById(R.id.ivLogo)
        btnTrackShipment = view.findViewById(R.id.btnTrackShipment)
        btnCreateShipment = view.findViewById(R.id.btnCreateShipment)
        btnNotifications = view.findViewById(R.id.btnNotifications)
        notificationBadge = view.findViewById(R.id.notificationBadge)

        // Set user name dynamically
        tvUserName.text = "أحمد العلي"
        
        // Show notification badge (for demo)
        notificationBadge.visibility = View.VISIBLE
    }

    private fun setupCounters() {
        animateCounter(tvTotalShipments, 124)
        animateCounter(tvActiveShipments, 5)
        animateCounter(tvCompletedShipments, 118)
        animateCounter(tvPendingShipments, 1)
        animateCounter(tvInProgressShipments, 3)
        animateCounter(tvDelayedShipments, 0)
    }

    private fun setupClickListeners() {
        // Main create shipment button
        cardCreateShipment.setOnClickListener {
            // Navigate to Create Shipment screen
            // findNavController().navigate(R.id.action_customerDashboard_to_createShipment)
        }

        tvSeeAll.setOnClickListener {
            // Navigate to all shipments
            // findNavController().navigate(R.id.action_customerDashboard_to_allShipments)
        }

        // Quick Actions
        actionTrackShipment.setOnClickListener {
            // Navigate to Track Shipment screen
            // findNavController().navigate(R.id.action_customerDashboard_to_trackShipment)
        }

        actionGetQuote.setOnClickListener {
            // Navigate to Get Quote screen
            // findNavController().navigate(R.id.action_customerDashboard_to_getQuote)
        }

        actionPayment.setOnClickListener {
            // Navigate to Payment screen
            // findNavController().navigate(R.id.action_customerDashboard_to_payment)
        }

        actionSupport.setOnClickListener {
            // Navigate to Support screen
            // findNavController().navigate(R.id.action_customerDashboard_to_support)
        }

        // Shortcuts
        btnTrackShipment.setOnClickListener {
            // Navigate to Track Shipment screen
            // findNavController().navigate(R.id.action_customerDashboard_to_trackShipment)
        }

        btnCreateShipment.setOnClickListener {
            // Navigate to Create Shipment screen
            // findNavController().navigate(R.id.action_customerDashboard_to_createShipment)
        }

        btnNotifications.setOnClickListener {
            // Navigate to Notifications screen
            // findNavController().navigate(R.id.action_customerDashboard_to_notifications)
        }

        // Enhanced Welcome Section Listeners
        ivProfilePicture.setOnClickListener {
            // Navigate to Profile screen
            // findNavController().navigate(R.id.action_customerDashboard_to_profile)
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = etSearch.text.toString().trim()
                if (searchQuery.isNotEmpty()) {
                    // Perform search
                    performSearch(searchQuery)
                }
                true
            }
            false
        }

        ivQRCode.setOnClickListener {
            // Open QR Scanner
            // findNavController().navigate(R.id.action_customerDashboard_to_qrScanner)
        }

        ivLogo.setOnClickListener {
            // Show app info or refresh dashboard
            // Could show a dialog with app version or refresh data
        }
    }

    private fun performSearch(query: String) {
        // Implement search functionality
        // This could filter shipments, navigate to search results, etc.
        // For now, just show a toast or log
        android.util.Log.d("CustomerDashboard", "Searching for: $query")
        
        // Example: Navigate to search results with query
        // findNavController().navigate(R.id.action_customerDashboard_to_searchResults, 
        //     Bundle().apply { putString("search_query", query) })
    }


    private fun animateCounter(textView: TextView, targetValue: Int, suffix: String = "") {
        val animator = ValueAnimator.ofInt(0, targetValue)
        animator.apply {
            duration = 1200
            addUpdateListener { animation ->
                textView.text = "${animation.animatedValue}$suffix"
            }
            doOnEnd {
                textView.text = "$targetValue$suffix"
            }
            start()
        }
    }



}
