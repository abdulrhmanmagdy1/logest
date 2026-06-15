package com.edham.logistics.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import com.edham.logistics.R
import com.edham.logistics.app.UserRole
import com.edham.logistics.ui.home.driver.DeliveryProofFragment
import com.edham.logistics.ui.home.driver.DriverDashboardNewFragment
import com.edham.logistics.ui.home.driver.DriverLogBookFragment
import com.edham.logistics.ui.home.driver.DriverSurveyFragment
import com.edham.logistics.ui.screens.FeatureActivity
import com.edham.logistics.ui.screens.FeatureActivity.FeatureKind
import dagger.hilt.android.AndroidEntryPoint

/**
 * Driver home — operational dashboard for drivers.
 */
@AndroidEntryPoint
class DriverHomeActivity : BaseRoleHomeActivity() {

    override val role: UserRole = UserRole.DRIVER
    override val menuRes: Int = R.menu.menu_drawer_driver
    override val toolbarTitle: String get() = getString(R.string.role_driver)

    override fun buildDashboard(parent: LinearLayout) {
        val fragment = DriverDashboardNewFragment()
        supportFragmentManager.beginTransaction()
            .replace(contentContainer.id, fragment)
            .commit()
    }

    override fun onCustomMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                contentContainer.removeAllViews()
                val fragment = DriverDashboardNewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(contentContainer.id, fragment)
                    .commit()
                return true
            }
            R.id.nav_mission -> {
                startActivity(Intent(this, com.edham.logistics.ui.home.driver.DriverMissionActivity::class.java))
                return true
            }
            R.id.nav_log_book -> {
                contentContainer.removeAllViews()
                val fragment = DriverLogBookFragment()
                supportFragmentManager.beginTransaction()
                    .replace(contentContainer.id, fragment)
                    .commit()
                return true
            }
            R.id.nav_proof -> {
                contentContainer.removeAllViews()
                val proofFragment = DeliveryProofFragment()
                supportFragmentManager.beginTransaction()
                    .replace(contentContainer.id, proofFragment)
                    .commit()
                return true
            }
            R.id.nav_chat -> {
                startActivity(FeatureActivity.intent(this, FeatureKind.CHAT))
                return true
            }
            R.id.nav_survey -> {
                contentContainer.removeAllViews()
                val surveyFragment = DriverSurveyFragment()
                supportFragmentManager.beginTransaction()
                    .replace(contentContainer.id, surveyFragment)
                    .commit()
                return true
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, com.edham.logistics.ui.screens.DriverSettingsActivity::class.java))
                return true
            }
            else -> return false
        }
    }
}
