package com.edham.logistics.ui.home

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.home.accountant.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

/**
 * Accountant home — elite financial command center.
 */
@AndroidEntryPoint
class AccountantHomeActivity : BaseActivity() {

    private lateinit var bottomNav: BottomNavigationView

    private val viewModel: AccountantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountant_home_premium)

        bottomNav = findViewById(R.id.bottom_nav)
        val drawer = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<com.google.android.material.navigation.NavigationView>(R.id.nav_view)
        
        setupNavigation()
        setupDrawer(drawer, navView)
        observeBadges()

        if (savedInstanceState == null) {
            openFragment(AccountantDashboardNewFragment())
        }
        
        viewModel.loadSettlements()
        viewModel.loadWorkshopRequests()
    }

    private fun observeBadges() {
        viewModel.settlements.observe(this) { list ->
            val count = list.size
            if (count > 0) {
                bottomNav.getOrCreateBadge(R.id.nav_driver).apply {
                    number = count
                    backgroundColor = getColor(R.color.ed_rust)
                }
            } else {
                bottomNav.removeBadge(R.id.nav_driver)
            }
        }

        viewModel.workshopRequests.observe(this) { list ->
            val count = list.size
            if (count > 0) {
                bottomNav.getOrCreateBadge(R.id.nav_workshop).apply {
                    number = count
                    backgroundColor = getColor(R.color.ed_copper_new)
                }
            } else {
                bottomNav.removeBadge(R.id.nav_workshop)
            }
        }
    }

    private fun setupDrawer(drawer: androidx.drawerlayout.widget.DrawerLayout, navView: com.google.android.material.navigation.NavigationView) {
        navView.setNavigationItemSelectedListener { item ->
            drawer.closeDrawer(androidx.core.view.GravityCompat.START)
            when (item.itemId) {
                R.id.nav_receipts -> openFragment(ReceiptVoucherFragment())
                R.id.nav_reports -> openFragment(AccountantReportsFragment())
                R.id.nav_logout -> {
                    com.edham.logistics.app.AuthSession.get(this).signOut()
                    finish()
                }
            }
            true
        }
    }

    private fun setupNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_dash -> AccountantDashboardNewFragment()
                R.id.nav_driver -> DriverSettlementFragment()
                R.id.nav_debt -> DebtAgingFragment()
                R.id.nav_workshop -> WorkshopApprovalsFragment()
                R.id.nav_soa -> SoAFragment()
                R.id.nav_notifications -> com.edham.logistics.ui.home.accountant.AccountantNotificationsFragment()
                else -> AccountantDashboardNewFragment()
            }
            openFragment(fragment)
            true
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
