package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.google.android.material.button.MaterialButton

class SupervisorSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_supervisor_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupProfile(view)
        setupRows(view)
        
        view.findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            AuthSession.get(requireContext()).signOut()
            requireActivity().finish() // Simple exit for now
        }
    }

    private fun setupProfile(view: View) {
        val session = AuthSession.get(requireContext())
        val name = session.displayName ?: "مستخدم إيدام"
        
        view.findViewById<TextView>(R.id.tvProfileName).text = name
        view.findViewById<TextView>(R.id.tvProfileInitials).text = name.take(1).uppercase()
        view.findViewById<TextView>(R.id.tvProfileRole).text = when(session.role) {
            com.edham.logistics.app.UserRole.SUPERVISOR -> "مشرف عمليات"
            com.edham.logistics.app.UserRole.ACCOUNTANT -> "محاسب مالي"
            com.edham.logistics.app.UserRole.DRIVER -> "سائق محترف"
            com.edham.logistics.app.UserRole.WORKSHOP -> "فني صيانة"
            else -> "مستخدم إيدام"
        }
    }

    private fun setupRows(view: View) {
        // Notifications
        view.findViewById<View>(R.id.rowNotifications).apply {
            findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_notification)
            findViewById<TextView>(R.id.tvTitle).text = "التنبيهات"
            setOnClickListener { showFeatureComingSoon() }
        }

        // Language
        view.findViewById<View>(R.id.rowLanguage).apply {
            findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_language)
            findViewById<TextView>(R.id.tvTitle).text = "اللغة"
            setOnClickListener { showFeatureComingSoon() }
        }

        // Dark Mode
        view.findViewById<View>(R.id.rowDarkMode).apply {
            findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_dark_mode)
            findViewById<TextView>(R.id.tvTitle).text = "المظهر الداكن"
            setOnClickListener { showFeatureComingSoon() }
        }

        // Security
        view.findViewById<View>(R.id.rowChangePassword).apply {
            findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_lock)
            findViewById<TextView>(R.id.tvTitle).text = "تغيير كلمة المرور"
            setOnClickListener { showFeatureComingSoon() }
        }

        // Privacy
        view.findViewById<View>(R.id.rowPrivacyPolicy).apply {
            findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_security)
            findViewById<TextView>(R.id.tvTitle).text = "سياسة الخصوصية"
            setOnClickListener { 
                startActivity(android.content.Intent(requireContext(), com.edham.logistics.ui.screens.PrivacyPolicyActivity::class.java))
            }
        }
    }

    private fun showFeatureComingSoon() {
        Toast.makeText(context, "هذه الميزة ستتوفر في التحديث القادم الخاص بإدارة التفضيلات", Toast.LENGTH_SHORT).show()
    }
}
