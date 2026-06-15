package com.edham.logistics.ui.home.supervisor

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
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Supervisor dashboard — full dark-themed dispatch control center.
 */
@AndroidEntryPoint
class SupervisorDashboardActivity : BaseActivity() {

    private val viewModel: SupervisorViewModel by viewModels()
    @javax.inject.Inject lateinit var networkMonitor: com.edham.logistics.core.network.NetworkMonitor
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbarTitle: TextView
    private lateinit var session: AuthSession
    private val healthHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var offlineSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_dashboard_new)

        session = AuthSession.get(this)

        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbarTitle = findViewById(R.id.topbar_title)

        findViewById<View>(R.id.btn_menu).setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END)
            } else {
                drawer.openDrawer(GravityCompat.END)
            }
        }

        bindNavHeader()
        setupNavigation()
        setupPulsingIndicator()
        observeHealth()
        monitorConnectivity()
        observeBadges()

        if (savedInstanceState == null) {
            openFragment(SupervisorDashboardNewFragment(), "لوحة القيادة")
            navigationView.setCheckedItem(R.id.nav_dashboard)
        }
        
        startHealthMonitoring()
    }

    private fun setupPulsingIndicator() {
        val dot = findViewById<View>(R.id.status_dot_pulse) ?: return
        val animation = android.view.animation.AlphaAnimation(1.0f, 0.4f).apply {
            duration = 1000
            repeatMode = android.view.animation.Animation.REVERSE
            repeatCount = android.view.animation.Animation.INFINITE
        }
        dot.startAnimation(animation)
    }

    private fun monitorConnectivity() {
        lifecycleScope.launch {
            networkMonitor.isConnected.collectLatest { connected ->
                if (!connected) {
                    showOfflineGuard()
                } else {
                    hideOfflineGuard()
                }
            }
        }
    }

    private fun showOfflineGuard() {
        if (offlineSnackbar == null) {
            offlineSnackbar = Snackbar.make(findViewById(android.R.id.content), 
                "⚠️ انقطع الاتصال بالسيرفر. يرجى التحقق من الشبكة.", 
                Snackbar.LENGTH_INDEFINITE)
            offlineSnackbar?.setBackgroundTint(ContextCompat.getColor(this, R.color.status_error))
            offlineSnackbar?.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        offlineSnackbar?.show()
    }

    private fun hideOfflineGuard() {
        offlineSnackbar?.dismiss()
    }

    private fun observeHealth() {
        viewModel.systemLatency.observe(this) { ms ->
            val dot = findViewById<View>(R.id.status_dot_pulse) ?: return@observe
            val label = findViewById<TextView>(R.id.right_actions)?.findViewById<TextView>(R.id.label_status) ?: return@observe
            
            when {
                ms == 0L -> {
                    dot.backgroundTintList = ContextCompat.getColorStateList(this, R.color.text_tertiary)
                    label.text = "جاري الاتصال..."
                }
                ms < 300 -> {
                    dot.backgroundTintList = ContextCompat.getColorStateList(this, R.color.status_success)
                    label.text = "الأسطول نشط"
                }
                ms < 800 -> {
                    dot.backgroundTintList = ContextCompat.getColorStateList(this, R.color.status_warning)
                    label.text = "اتصال غير مستقر"
                }
                else -> {
                    dot.backgroundTintList = ContextCompat.getColorStateList(this, R.color.status_error)
                    label.text = "تأخر في الاستجابة"
                }
            }
        }
    }

    private fun observeBadges() {
        viewModel.alerts.observe(this) { list ->
            val count = list.size
            if (count > 0) {
                // In a real app with a side drawer, we might show badges on specific menu items
                // This is a placeholder for menu item badge logic if using a NavigationRail or BottomNav
            }
        }
    }

    private fun startHealthMonitoring() {
        healthHandler.post(object : Runnable {
            override fun run() {
                viewModel.refreshSystemHealth()
                healthHandler.postDelayed(this, 5000) // Update health every 5s
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        healthHandler.removeCallbacksAndMessages(null)
    }

    private fun bindNavHeader() {
        val header = navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.navHeaderName).text =
            session.displayName ?: getString(R.string.role_supervisor)
        header.findViewById<TextView>(R.id.navHeaderEmail).text =
            session.email.orEmpty()
        header.findViewById<TextView>(R.id.navHeaderRole).text =
            getString(R.string.role_supervisor)
    }

    private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { item ->
            drawer.closeDrawer(GravityCompat.END)
            handleNavItem(item)
            true
        }
    }

    private fun handleNavItem(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_dashboard -> openFragment(SupervisorDashboardNewFragment(), "لوحة القيادة")
            R.id.nav_tracking -> openFragment(SupervisorTrackingNewFragment(), "تتبع السائقين")
            R.id.nav_fleet -> openFragment(SupervisorFleetNewFragment(), "إدارة الأسطول")
            R.id.nav_drivers -> openFragment(SupervisorDriversNewFragment(), "إدارة السائقين")
            R.id.nav_loads -> openFragment(SupervisorLoadsNewFragment(), "إدارة الحمولات")
            R.id.nav_invoices -> openFragment(SupervisorInvoicesNewFragment(), "الفواتير والمديونيات")
            R.id.nav_maintenance -> openFragment(SupervisorMaintenanceNewFragment(), "الورشة والصيانة")
            R.id.nav_parts -> openFragment(SupervisorPartsNewFragment(), "إدارة القطع")
            R.id.nav_reports -> openFragment(SupervisorReportsNewFragment(), "التقارير والتحليلات")
            R.id.nav_survey -> openFragment(SupervisorSurveyNewFragment(), "استبيان السائقين")
            R.id.nav_settings -> openFragment(SupervisorSettingsFragment(), "الإعدادات")
            R.id.nav_logout -> logout()
        }
    }

    private fun openFragment(fragment: Fragment, title: String) {
        toolbarTitle.text = title
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
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
