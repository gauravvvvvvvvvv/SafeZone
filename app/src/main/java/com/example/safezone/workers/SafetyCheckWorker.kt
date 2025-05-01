package com.example.safezone.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.safezone.models.Alert
import com.example.safezone.utils.NotificationHelper

class SafetyCheckWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    
    override fun doWork(): Result {
        // In a real app, this would fetch data from a server
        // For this example, we'll just simulate a safety check alert
        
        // Check if there's any alert to show (could be from a backend or SharedPreferences)
        val shouldShowAlert = System.currentTimeMillis() % 2 == 0L // Just a demo condition
        
        if (shouldShowAlert) {
            val safetyAlert = Alert(
                id = System.currentTimeMillis().toInt(),
                title = "Daily Safety Update",
                description = "Your area is currently safe. No incidents reported in the last 24 hours.",
                location = "Your neighborhood",
                severity = "Low",
                timestamp = System.currentTimeMillis()
            )
            
            // Show a notification for this safety update
            NotificationHelper.showAlertNotification(context, safetyAlert)
        }
        
        // Indicate success
        return Result.success()
    }
}
