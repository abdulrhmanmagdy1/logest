package com.edham.logistics.ui.home.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.ui.auth.LoginActivity
import com.edham.logistics.ui.screens.CustomerSupportActivity

class CustomerProfileActivity : AppCompatActivity() {

    private lateinit var session: AuthSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_profile)

        session = AuthSession.get(this)

        setupHero()
        setupMenu()
        
        findViewById<View>(R.id.profileToolbar).setOnClickListener { finish() }
    }

    private fun setupHero() {
        val name = session.displayName ?: "عميل إدهام"
        findViewById<TextView>(R.id.profileName).text = name
        findViewById<TextView>(R.id.profilePhone).text = session.phone ?: "+966 50 000 0000"
        findViewById<TextView>(R.id.profileAvatar).text = name.take(1).uppercase()
    }

    private fun setupMenu() {
        // Company
        bindMenuItem(R.id.menuCompany, "بيانات الشركة", R.drawable.ic_image) {
            // TODO
        }
        
        // Settings
        bindMenuItem(R.id.menuSettings, "الإعدادات العامة", R.drawable.ic_settings) {
            startActivity(Intent(this, com.edham.logistics.ui.screens.CustomerSettingsActivity::class.java))
        }
        
        // Privacy
        bindMenuItem(R.id.menuPrivacy, "الأمان والخصوصية", R.drawable.ic_lock) {
            // TODO
        }
        
        // Support
        bindMenuItem(R.id.menuSupport, "مركز المساعدة", R.drawable.ic_help) {
            startActivity(Intent(this, CustomerSupportActivity::class.java))
        }
        
        // Logout
        bindMenuItem(R.id.menuLogout, "تسجيل الخروج", R.drawable.ic_logout, isDanger = true) {
            session.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }
    }

    private fun bindMenuItem(viewId: Int, title: String, iconRes: Int, isDanger: Boolean = false, onClick: () -> Unit) {
        val view = findViewById<View>(viewId)
        view.findViewById<TextView>(R.id.menuTitle).text = title
        view.findViewById<ImageView>(R.id.menuIcon).setImageResource(iconRes)
        
        if (isDanger) {
            view.findViewById<TextView>(R.id.menuTitle).setTextColor(resources.getColor(R.color.status_error, null))
            view.findViewById<ImageView>(R.id.menuIcon).imageTintList = android.content.res.ColorStateList.valueOf(resources.getColor(R.color.status_error, null))
            view.findViewById<ImageView>(R.id.menuIcon).backgroundTintList = android.content.res.ColorStateList.valueOf(resources.getColor(R.color.error_container, null))
        }
        
        view.setOnClickListener { onClick() }
    }
}
