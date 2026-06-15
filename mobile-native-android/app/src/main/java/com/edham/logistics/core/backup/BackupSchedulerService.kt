package com.edham.logistics.core.backup

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backup Scheduler Service - Handles automatic backup scheduling
 * Provides daily automatic backup functionality
 */
@Singleton
class BackupSchedulerService @Inject constructor(
    private val context: Context
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Backup scheduling constants
    private val BACKUP_REQUEST_CODE = 1001
    private val BACKUP_ACTION = "com.edham.logistics.BACKUP_ACTION"
    
    // Backup state
    private var isScheduled = false
    private var nextBackupTime: Long? = null

    /**
     * Schedule daily backup
     */
    fun scheduleDailyBackup(backupTime: String = "02:00") {
        try {
            cancelScheduledBackup()
            
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                
                // Parse backup time
                val timeParts = backupTime.split(":")
                if (timeParts.size == 2) {
                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    set(Calendar.MINUTE, timeParts[1].toInt())
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    
                    // If time is in the past, schedule for tomorrow
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            }
            
            val intent = Intent(BACKUP_ACTION).apply {
                setClass(context, BackupReceiver::class.java)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BACKUP_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Schedule exact alarm for backup
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            
            isScheduled = true
            nextBackupTime = calendar.timeInMillis
            
            // Schedule recurring alarm
            scheduleRecurringBackup(calendar)
            
        } catch (e: Exception) {
            // Handle scheduling error
        }
    }

    /**
     * Schedule recurring backup
     */
    private fun scheduleRecurringBackup(firstBackup: Calendar) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            // Create intent for recurring backup
            val intent = Intent(BACKUP_ACTION).apply {
                putExtra("RECURRING", true)
                setClass(context, BackupReceiver::class.java)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BACKUP_REQUEST_CODE + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Schedule recurring alarm every 24 hours
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                firstBackup.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            
        } catch (e: Exception) {
            // Handle recurring scheduling error
        }
    }

    /**
     * Cancel scheduled backup
     */
    fun cancelScheduledBackup() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            // Cancel exact backup
            val exactIntent = Intent(BACKUP_ACTION)
            val exactPendingIntent = PendingIntent.getBroadcast(
                context,
                BACKUP_REQUEST_CODE,
                exactIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            
            exactPendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
            
            // Cancel recurring backup
            val recurringIntent = Intent(BACKUP_ACTION).apply {
                putExtra("RECURRING", true)
            }
            val recurringPendingIntent = PendingIntent.getBroadcast(
                context,
                BACKUP_REQUEST_CODE + 1,
                recurringIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            
            recurringPendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
            
            isScheduled = false
            nextBackupTime = null
            
        } catch (e: Exception) {
            // Handle cancel error
        }
    }

    /**
     * Check if backup is scheduled
     */
    fun isBackupScheduled(): Boolean {
        return isScheduled
    }

    /**
     * Get next scheduled backup time
     */
    fun getNextScheduledBackup(): Long? {
        return nextBackupTime
    }

    /**
     * Get next scheduled backup time as formatted string
     */
    fun getNextScheduledBackupFormatted(): String {
        return nextBackupTime?.let { time ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateFormat.format(Date(time))
        } ?: "Not scheduled"
    }

    /**
     * Schedule immediate backup
     */
    fun scheduleImmediateBackup() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            val intent = Intent(BACKUP_ACTION).apply {
                putExtra("IMMEDIATE", true)
                setClass(context, BackupReceiver::class.java)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BACKUP_REQUEST_CODE + 2,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Schedule for 5 seconds from now
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 5000,
                pendingIntent
            )
            
        } catch (e: Exception) {
            // Handle immediate scheduling error
        }
    }

    /**
     * Schedule backup with delay
     */
    fun scheduleBackupWithDelay(delayMinutes: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            val intent = Intent(BACKUP_ACTION).apply {
                putExtra("DELAYED", true)
                setClass(context, BackupReceiver::class.java)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BACKUP_REQUEST_CODE + 3,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Schedule for specified delay
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (delayMinutes * 60 * 1000),
                pendingIntent
            )
            
        } catch (e: Exception) {
            // Handle delayed scheduling error
        }
    }

    /**
     * Get backup schedule info
     */
    fun getBackupScheduleInfo(): BackupScheduleInfo {
        return BackupScheduleInfo(
            isScheduled = isScheduled,
            nextBackupTime = nextBackupTime,
            nextBackupFormatted = getNextScheduledBackupFormatted(),
            canScheduleExact = canScheduleExactAlarm(),
            hasPermission = hasAlarmPermission()
        )
    }

    /**
     * Check if can schedule exact alarm
     */
    private fun canScheduleExactAlarm(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Check if has alarm permission
     */
    private fun hasAlarmPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Request alarm permission
     */
    fun requestAlarmPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Open alarm settings for user to grant permission
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = android.net.Uri.parse("package:" + context.packageName)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    /**
     * Cleanup service
     */
    fun cleanup() {
        cancelScheduledBackup()
        coroutineScope.cancel()
    }
}

/**
 * Backup Receiver - Handles backup alarm triggers
 */
class BackupReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when {
            intent.getBooleanExtra("IMMEDIATE", false) -> {
                // Handle immediate backup
                handleImmediateBackup(context)
            }
            intent.getBooleanExtra("DELAYED", false) -> {
                // Handle delayed backup
                handleDelayedBackup(context)
            }
            intent.getBooleanExtra("RECURRING", false) -> {
                // Handle recurring backup
                handleRecurringBackup(context)
            }
            else -> {
                // Handle regular scheduled backup
                handleScheduledBackup(context)
            }
        }
    }
    
    private fun handleImmediateBackup(context: Context) {
        // Trigger immediate backup
        // This would integrate with BackupManager
    }
    
    private fun handleDelayedBackup(context: Context) {
        // Trigger delayed backup
        // This would integrate with BackupManager
    }
    
    private fun handleRecurringBackup(context: Context) {
        // Trigger recurring backup
        // This would integrate with BackupManager
    }
    
    private fun handleScheduledBackup(context: Context) {
        // Trigger scheduled backup
        // This would integrate with BackupManager
    }
}

/**
 * Data classes
 */
data class BackupScheduleInfo(
    val isScheduled: Boolean,
    val nextBackupTime: Long?,
    val nextBackupFormatted: String,
    val canScheduleExact: Boolean,
    val hasPermission: Boolean
)
