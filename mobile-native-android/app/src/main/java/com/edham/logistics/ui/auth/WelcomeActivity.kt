package com.edham.logistics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.edham.logistics.ui.home.CustomerHomeActivity
import com.google.android.material.button.MaterialButton

/**
 * Confirmation screen shown immediately after a successful customer
 * registration. Greets the user by their first name and routes them to
 * the customer home.
 */
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val firstName = intent.getStringExtra(EXTRA_NAME).orEmpty()

        findViewById<TextView>(R.id.welcomeTitle).text =
            getString(R.string.signup_welcome_title, firstName)
        findViewById<TextView>(R.id.welcomeMessage).text =
            getString(R.string.signup_welcome_message)

        findViewById<MaterialButton>(R.id.btnContinue).setOnClickListener {
            startActivity(Intent(this, CustomerHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }
    }

    companion object { const val EXTRA_NAME = "extra_first_name" }
}
