package com.edham.logistics.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.ui.BaseActivity
import java.util.Locale

class CustomerSettingsActivity : BaseActivity() {

    private lateinit var session: AuthSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_customer_settings)

        session = AuthSession.get(this)
        val prefs = getSharedPreferences("customer_settings", MODE_PRIVATE)

        // 1. Back Button
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.settingsToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. UI Binding
        val switchPush = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchPushNotifications)
        val switchEmail = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchEmailNotifications)
        val rgLanguage = findViewById<RadioGroup>(R.id.rgLanguage)
        val rbArabic = findViewById<android.widget.RadioButton>(R.id.rbArabic)
        val rbEnglish = findViewById<android.widget.RadioButton>(R.id.rbEnglish)
        val rbDark = findViewById<android.widget.RadioButton>(R.id.rbDark)
        val rbLight = findViewById<android.widget.RadioButton>(R.id.rbLight)
        val btnSave = findViewById<View>(R.id.btnSaveSettings)

        // 3. Load Current State
        switchPush.isChecked = prefs.getBoolean("push_enabled", true)
        switchEmail.isChecked = prefs.getBoolean("email_enabled", true)
        
        val currentLang = prefs.getString("app_lang", "ar")
        if (currentLang == "ar") rbArabic.isChecked = true else rbEnglish.isChecked = true

        val currentTheme = prefs.getString("app_theme", "dark")
        if (currentTheme == "dark") rbDark.isChecked = true else rbLight.isChecked = true

        // 4. Save Logic
        btnSave.setOnClickListener {
            val selectedLang = if (rbArabic.isChecked) "ar" else "en"
            val selectedTheme = if (rbDark.isChecked) "dark" else "light"
            
            prefs.edit().apply {
                putBoolean("push_enabled", switchPush.isChecked)
                putBoolean("email_enabled", switchEmail.isChecked)
                putString("app_lang", selectedLang)
                putString("app_theme", selectedTheme)
                apply()
            }
            
            Toast.makeText(this, "تم حفظ الإعدادات ✅", Toast.LENGTH_SHORT).show()
            
            // Apply language change
            com.edham.logistics.util.LocaleHelper.setLocale(this, selectedLang)

            // Apply theme (In a real app, use AppCompatDelegate.setDefaultNightMode)
            if (selectedTheme == "dark") {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
            }
            
            // Restart to home to apply changes globally
            val intent = Intent(this, com.edham.logistics.ui.home.CustomerHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setAppLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
