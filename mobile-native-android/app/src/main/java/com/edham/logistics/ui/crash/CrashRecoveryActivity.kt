package com.edham.logistics.ui.crash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Crash Recovery Activity - نشاط بسيط جداً للتعافي من الكراش
 * مش يعتمد على أي theme معقد أو initialization من التطبيق
 * هو الـ launcher الآن - لو فيه كراش يعرضه، وإلا يفتح التطبيق
 */
class CrashRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Use minimal theme - no custom app theme
        try { setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight) } catch (_: Throwable) {}
        super.onCreate(savedInstanceState)

        val crashText = getCrashText()
        if (crashText == null || crashText == "لا يوجد تقرير كراش") {
            // No crash - launch the app normally
            try {
                val intent = Intent(this, com.edham.logistics.ui.splash.SplashActivity::class.java)
                startActivity(intent)
                finish()
            } catch (t: Throwable) {
                // Even SplashActivity fails - show error
                showMinimalError("فشل فتح التطبيق:\n${t.stackTraceToString()}")
            }
            return
        }

        // There's a crash - show it
        showCrashDialog(crashText)
    }

    private fun showCrashDialog(crashText: String) {
        // Build UI manually - no XML layout
        val root = ScrollView(this)
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "تقرير الكراش الأخير"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        container.addView(title)

        val crashTextView = TextView(this).apply {
            text = crashText
            textSize = 12f
            setPadding(0, 0, 0, 24)
        }
        container.addView(crashTextView)

        val btnCopy = Button(this).apply {
            text = "نسخ التقرير"
            setOnClickListener {
                try {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("crash", crashText))
                    android.widget.Toast.makeText(this@CrashRecoveryActivity, "تم النسخ", android.widget.Toast.LENGTH_SHORT).show()
                } catch (_: Throwable) {}
            }
        }
        container.addView(btnCopy)

        val btnClear = Button(this).apply {
            text = "مسح ومحاولة فتح التطبيق"
            setOnClickListener {
                try { clearCrash() } catch (_: Throwable) {}
                try {
                    val intent = Intent(this@CrashRecoveryActivity, com.edham.logistics.ui.splash.SplashActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (_: Throwable) {}
            }
        }
        container.addView(btnClear)

        root.addView(container)
        setContentView(root)
    }

    private fun showMinimalError(message: String) {
        val tv = TextView(this).apply {
            text = message
            textSize = 14f
            setPadding(32, 32, 32, 32)
        }
        setContentView(tv)
    }

    private fun getCrashText(): String? {
        return try {
            val prefs = getSharedPreferences("edham_crash_prefs", MODE_PRIVATE)
            prefs.getString("last_crash", null)
        } catch (_: Throwable) {
            null
        }
    }

    private fun clearCrash() {
        try {
            getSharedPreferences("edham_crash_prefs", MODE_PRIVATE)
                .edit().remove("last_crash").commit()
        } catch (_: Throwable) {}
    }
}
