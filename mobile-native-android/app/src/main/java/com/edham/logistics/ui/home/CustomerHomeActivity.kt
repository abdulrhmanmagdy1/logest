package com.edham.logistics.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.UserRole
import com.edham.logistics.ui.auth.LoginActivity
import com.edham.logistics.ui.home.customer.CustomerDashboardProductionFragment
import com.edham.logistics.ui.screens.CustomerInvoicesActivity
import com.edham.logistics.ui.screens.CustomerSettingsActivity
import com.edham.logistics.ui.screens.CustomerSupportActivity
import com.edham.logistics.ui.screens.FeatureActivity
import com.edham.logistics.ui.screens.FeatureActivity.FeatureKind
import com.google.android.material.navigation.NavigationView

/**
 * Customer home activity using high-fidelity production layouts and fragments.
 * Supports multiple users via AuthSession and ensures no hardcoded mock data in UI logic.
 */
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.home.customer.wizard.CargoWizardActivity
import com.edham.logistics.ui.screens.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerHomeActivity : BaseActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var session: AuthSession
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_home_production)

        session = AuthSession.get(this)

        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        bottomNav = findViewById(R.id.bottom_nav)
        toolbarTitle = findViewById(R.id.topbar_title)

        setupUI()
        
        if (savedInstanceState == null) {
            openFragment(CustomerDashboardProductionFragment())
        }
    }

    private fun setupUI() {
        // Toggle Menu
        findViewById<View>(R.id.btn_menu).setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }

        // Notification Bell
        findViewById<View>(R.id.btn_notification).setOnClickListener {
            startActivity(Intent(this, CustomerNotificationsActivity::class.java))
        }

        // Cart Icon
        findViewById<View>(R.id.btn_cart).setOnClickListener {
            startActivity(Intent(this, com.edham.logistics.ui.screens.FeatureActivity::class.java).apply {
                putExtra("FEATURE_KIND", "ORDERS")
            })
        }

        // Bottom Nav Logic
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> openFragment(CustomerDashboardProductionFragment())
                R.id.nav_track -> startActivity(Intent(this, TrackShipmentActivity::class.java))
                R.id.nav_new -> startActivity(Intent(this, CargoWizardActivity::class.java))
                R.id.nav_vault -> startActivity(Intent(this, CustomerInvoicesActivity::class.java))
                R.id.nav_support -> startActivity(Intent(this, CustomerSupportActivity::class.java))
            }
            true
        }

        // Sidebar Navigation
        navigationView.setNavigationItemSelectedListener { item ->
            drawer.closeDrawer(GravityCompat.START)
            handleNavigation(item)
            true
        }

        // Header binding (Multi-user support)
        val header = navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.navHeaderName).text = session.displayName ?: "عميل إدهام"
        header.findViewById<TextView>(R.id.navHeaderEmail).text = session.email.orEmpty()
        header.findViewById<TextView>(R.id.navHeaderRole).text = getString(R.string.role_customer)
    }

    private fun handleNavigation(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_home -> openFragment(CustomerDashboardProductionFragment())
            R.id.nav_new_shipment -> startActivity(Intent(this, CargoWizardActivity::class.java))
            R.id.nav_offers -> startActivity(Intent(this, com.edham.logistics.ui.screens.CustomerOffersActivity::class.java))
            R.id.nav_track -> startActivity(Intent(this, TrackShipmentActivity::class.java))
            R.id.nav_wallet -> startActivity(Intent(this, CustomerInvoicesActivity::class.java))
            R.id.nav_profile -> startActivity(Intent(this, com.edham.logistics.ui.home.customer.CustomerProfileActivity::class.java))
            R.id.nav_charter -> startActivity(Intent(this, com.edham.logistics.ui.screens.CharterActivity::class.java))
            R.id.nav_privacy -> startActivity(Intent(this, com.edham.logistics.ui.screens.PrivacyPolicyActivity::class.java))
            R.id.nav_support -> startActivity(Intent(this, CustomerSupportActivity::class.java))
            R.id.nav_logout -> logout()
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun logout() {
        session.signOut()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
