package com.example.safezone.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.safezone.receivers.AlarmReceiver
import com.example.safezone.workers.SafetyCheckWorker
import java.util.concurrent.TimeUnit

object SchedulingUtils {
    
    private const val SAFETY_CHECK_WORK_NAME = "safety_check_work"
    
    /**
     * Schedule a one-time alarm
     */
    fun scheduleAlarm(context: Context, timeInMillis: Long, requestCode: Int, title: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Set the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }
    }
    
    /**
     * Cancel a previously scheduled alarm
     */
    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
    
    /**
     * Schedule periodic background work using WorkManager
     */
    fun scheduleSafetyChecks(context: Context) {
        // Define constraints for the work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)  // Needs internet connection
            .build()
        
        // Create a periodic work request - runs once a day
        val safetyCheckWorkRequest = PeriodicWorkRequestBuilder<SafetyCheckWorker>(
            24, TimeUnit.HOURS  // Repeat interval
        )
            .setConstraints(constraints)
            .build()
        
        // Enqueue the work
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SAFETY_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,  // Keep existing work if it exists
            safetyCheckWorkRequest
        )
    }
    
    /**
     * Cancel the periodic safety checks
     */
    fun cancelSafetyChecks(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SAFETY_CHECK_WORK_NAME)
    }
}
