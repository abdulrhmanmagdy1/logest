package com.edham.logistics

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Temporary minimal entry-point for the app.
 *
 * The original [MainActivity] depended on Hilt, multiple navigation controllers
 * and dozens of fragments that are still being stabilized. Until the rest of
 * the codebase is restored, this stub ensures the app can launch and show a
 * recognizable screen so the rest of the build pipeline (signing, packaging,
 * install) keeps working.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = getString(R.string.welcome)
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }

        root.addView(title)
        root.addView(subtitle)
        setContentView(root)
    }
}
