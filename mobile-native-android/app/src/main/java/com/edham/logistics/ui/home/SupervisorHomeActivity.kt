package com.edham.logistics.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import com.edham.logistics.R
import com.edham.logistics.app.UserRole
import com.edham.logistics.ui.home.supervisor.SupervisorDashboardActivity

/**
 * Supervisor home — operational dashboard. Shows headline KPIs, dispatch
 * queue, fleet snapshot, and recent activity. Each list item is wired to
 * "coming soon" toasts until the full management screens are restored.
 */
class SupervisorHomeActivity : BaseRoleHomeActivity() {

    override val role: UserRole = UserRole.SUPERVISOR
    override val menuRes: Int = R.menu.menu_drawer_supervisor
    override val toolbarTitle: String get() = getString(R.string.role_supervisor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to new full dashboard
        startActivity(Intent(this, SupervisorDashboardActivity::class.java))
        finish()
    }

    override fun buildDashboard(parent: LinearLayout) {
        // Deprecated — new dashboard uses fragments
    }

    override fun onCustomMenuItemSelected(item: MenuItem): Boolean = false
}
