package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.ui.BaseActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    private lateinit var session: AuthSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        session = AuthSession.get(this)

        val etName = findViewById<TextInputEditText>(R.id.etEditName)
        val etPhone = findViewById<TextInputEditText>(R.id.etEditPhone)

        etName.setText(session.displayName)
        etPhone.setText(session.phone)

        findViewById<View>(R.id.toolbar).setOnClickListener { finish() }

        findViewById<View>(R.id.btnSaveProfile).setOnClickListener {
            val newName = etName.text.toString()
            val newPhone = etPhone.text.toString()
            
            if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
                session.displayName = newName
                session.phone = newPhone
                Toast.makeText(this, "تم تحديث البيانات بنجاح ✅", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "يرجى ملء كافة الحقول", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
