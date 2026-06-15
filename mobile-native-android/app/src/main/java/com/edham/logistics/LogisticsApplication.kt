package com.edham.logistics

import android.app.Application
import android.content.Context
import com.edham.logistics.core.crash.CrashReporter
import com.edham.logistics.core.di.ServiceLocator
import com.edham.logistics.core.utils.TokenManager
import com.edham.logistics.util.LocaleHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for Edham Logistics.
 */
@HiltAndroidApp
class LogisticsApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
        // Install crash reporter as early as possible
        try { CrashReporter.install(this) } catch (_: Throwable) {}
    }

    override fun onCreate() {
        try {
            super.onCreate()
        } catch (t: Throwable) {
            // Try to save crash even if super.onCreate fails
            try {
                getSharedPreferences("edham_crash_prefs", MODE_PRIVATE)
                    .edit().putString("last_crash", "super.onCreate() failed: ${t.stackTraceToString()}")
                    .commit()
            } catch (_: Throwable) {}
            throw t
        }

        // Re-ensure crash reporter is installed
        try { CrashReporter.install(this) } catch (_: Throwable) {}

        try {
            TokenManager.init(this)
        } catch (t: Throwable) {
            try { Timber.e(t, "TokenManager init failed") } catch (_: Throwable) {}
        }

        try {
            ServiceLocator.init(this)
        } catch (t: Throwable) {
            try { Timber.e(t, "ServiceLocator init failed") } catch (_: Throwable) {}
        }

        try {
            if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        } catch (_: Throwable) {}

        try {
            Timber.i("Edham Logistics app initialized — API: %s", BuildConfig.API_BASE_URL)
        } catch (_: Throwable) {}
    }
}
