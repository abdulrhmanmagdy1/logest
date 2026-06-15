package com.edham.logistics.ui.home.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.auth.LoginActivity
import com.edham.logistics.ui.screens.CustomerSupportActivity

class CustomerProfileActivity : BaseActivity() {

    private lateinit var session: AuthSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_profile)

        session = AuthSession.get(this)

        setupHero()
        setupMenu()
        
        // Solid Back Button
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.profileToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { 
            finish()
        }
    }

    private fun setupHero() {
        val name = session.displayName ?: getString(R.string.individual_client)
        val phone = session.phone ?: getString(R.string.phone_not_registered)
        
        findViewById<TextView>(R.id.profileName).text = name
        findViewById<TextView>(R.id.profilePhone).text = phone
        findViewById<TextView>(R.id.profileAvatar).text = name.trim().take(1).uppercase()
        
        // Dynamic Stats - Bind to session data if available
        findViewById<TextView>(R.id.statShipments).text = "0"
        findViewById<TextView>(R.id.statRating).text = "5.0"
        findViewById<TextView>(R.id.statYears).text = "جديد"
        
        findViewById<View>(R.id.btnChangeAvatar).setOnClickListener {
            Toast.makeText(this, "قريباً: تغيير الصورة الشخصية", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMenu() {
        // Edit Profile
        bindMenuItem(R.id.menuEditProfile, getString(R.string.menu_edit_profile), R.drawable.ic_person) {
            startActivity(Intent(this, com.edham.logistics.ui.screens.EditProfileActivity::class.java))
        }

        // About Us
        bindMenuItem(R.id.menuCompany, getString(R.string.menu_about_us), R.drawable.ic_info) {
            startActivity(Intent(this, com.edham.logistics.ui.screens.AboutUsActivity::class.java))
        }
        
        // Settings
        bindMenuItem(R.id.menuSettings, getString(R.string.menu_settings_general), R.drawable.ic_settings) {
            startActivity(Intent(this, com.edham.logistics.ui.screens.CustomerSettingsActivity::class.java))
        }
        
        // FAQ
        bindMenuItem(R.id.menuPrivacy, getString(R.string.menu_faq), R.drawable.ic_help) {
            startActivity(Intent(this, com.edham.logistics.ui.screens.FaqActivity::class.java))
        }

        // Charter
        bindMenuItem(R.id.menuCharter, getString(R.string.menu_charter), R.drawable.ic_certificate) {
            startActivity(Intent(this, com.edham.logistics.ui.screens.CharterActivity::class.java))
        }

        // Privacy Policy
        bindMenuItem(R.id.menuPrivacyPolicy, getString(R.string.menu_privacy_policy), R.drawable.ic_lock) {
            startActivity(Intent(this, com.edham.logistics.ui.screens.PrivacyPolicyActivity::class.java))
        }
        
        // Support
        bindMenuItem(R.id.menuSupport, getString(R.string.menu_help_center), R.drawable.ic_help) {
            startActivity(Intent(this, CustomerSupportActivity::class.java))
        }
        
        // Logout
        bindMenuItem(R.id.menuLogout, getString(R.string.menu_logout_label), R.drawable.ic_logout, isDanger = true) {
            session.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }
    }

    private fun bindMenuItem(containerId: Int, title: String, iconRes: Int, isDanger: Boolean = false, onClick: () -> Unit) {
        val container = findViewById<View>(containerId)
        val titleView = container.findViewById<TextView>(R.id.menuTitle)
        val iconView = container.findViewById<ImageView>(R.id.menuIcon)
        
        titleView.text = title
        iconView.setImageResource(iconRes)
        
        if (isDanger) {
            titleView.setTextColor(resources.getColor(R.color.status_error, null))
            iconView.imageTintList = android.content.res.ColorStateList.valueOf(resources.getColor(R.color.status_error, null))
        }
        
        container.setOnClickListener { onClick() }
    }
}
