package com.edham.logistics.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.util.LocaleHelper

abstract class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        // Ensure locale is applied before context is attached
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }
}
