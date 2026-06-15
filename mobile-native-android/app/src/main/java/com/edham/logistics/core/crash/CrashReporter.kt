package com.edham.logistics.core.crash

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Crash Reporter — يحفظ أي Exception غير معالج في ملف نصي
 * داخل تخزين التطبيق الخاص (filesDir/crashes/).
 *
 * استخدام:
 *   CrashReporter.install(applicationContext)
 *
 * لقراءة الكراش:
 *   adb pull /data/data/com.edham.logistics/files/crashes
 * أو من داخل التطبيق عن طريق `CrashReporter.getLastCrashText(ctx)`.
 */
object CrashReporter {

    private const val DIR_NAME = "crashes"
    private const val PREFS_NAME = "edham_crash_prefs"
    private const val KEY_LAST_CRASH = "last_crash"

    fun install(context: Context) {
        val appCtx = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val text = buildCrashText(thread, throwable)
                // 1) Save to SharedPreferences synchronously (commit) - الأكثر موثوقية
                try {
                    appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit().putString(KEY_LAST_CRASH, text).commit()
                } catch (_: Throwable) {}
                // 2) Also save to file
                try {
                    val dir = File(appCtx.filesDir, DIR_NAME).apply { mkdirs() }
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    File(dir, "crash_$timestamp.txt").writeText(text)
                } catch (_: Throwable) {}
            } catch (_: Throwable) {
                // best effort - never crash the crash handler
            }
            previous?.uncaughtException(thread, throwable)
        }
    }

    private fun buildCrashText(thread: Thread, throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        pw.println("=== Edham Logistics Crash Report ===")
        pw.println("Time: ${Date()}")
        pw.println("Thread: ${thread.name}")
        pw.println("Android: ${android.os.Build.VERSION.SDK_INT} (${android.os.Build.VERSION.RELEASE})")
        pw.println("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
        pw.println("=== Stack Trace ===")
        throwable.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    /** نص آخر كراش (لعرضه داخل شاشة دعم). null لو مفيش كراشات. */
    fun getLastCrashText(context: Context): String? {
        // Prefer SharedPreferences (sync, reliable)
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val text = prefs.getString(KEY_LAST_CRASH, null)
            if (!text.isNullOrEmpty()) return text
        } catch (_: Throwable) {}
        // Fallback: latest file
        return try {
            val dir = File(context.filesDir, DIR_NAME)
            if (!dir.exists()) return null
            val latest = dir.listFiles()
                ?.filter { it.isFile && it.name.startsWith("crash_") }
                ?.maxByOrNull { it.lastModified() }
                ?: return null
            latest.readText()
        } catch (_: Throwable) { null }
    }

    fun clear(context: Context) {
        try {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().remove(KEY_LAST_CRASH).commit()
        } catch (_: Throwable) {}
        try {
            val dir = File(context.filesDir, DIR_NAME)
            dir.listFiles()?.forEach { it.delete() }
        } catch (_: Throwable) {}
    }
}
