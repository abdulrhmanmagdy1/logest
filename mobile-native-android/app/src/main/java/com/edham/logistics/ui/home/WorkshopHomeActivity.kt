package com.edham.logistics.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.home.workshop.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopHomeActivity : BaseActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_generic)

        bottomNav = findViewById(R.id.bottom_nav)
        val drawer = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<com.google.android.material.navigation.NavigationView>(R.id.nav_view)
        
        setupNavigation()
        setupDrawer(drawer, navView)

        if (savedInstanceState == null) {
            openFragment(WorkshopDashboardFragment())
        }
    }

    private fun setupDrawer(drawer: androidx.drawerlayout.widget.DrawerLayout, navView: com.google.android.material.navigation.NavigationView) {
        navView.setNavigationItemSelectedListener { item ->
            drawer.closeDrawer(androidx.core.view.GravityCompat.END)
            when (item.itemId) {
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
            when (item.itemId) {
                R.id.nav_home -> openFragment(WorkshopDashboardFragment())
                R.id.nav_inventory -> openFragment(WorkshopInventoryFragment())
                R.id.nav_maintenance -> openFragment(WorkshopGroundedFragment())
                R.id.nav_alerts -> openFragment(WorkshopPredictiveFragment())
                R.id.nav_parts -> openFragment(WorkshopProcurementFragment())
            }
            true
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
