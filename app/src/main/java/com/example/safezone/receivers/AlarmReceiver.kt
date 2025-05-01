package com.example.safezone.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.safezone.models.Alert
import com.example.safezone.utils.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Safety Alert"
        val message = intent.getStringExtra("message") ?: "New safety alert in your area"
        
        // Create an alert from the received data
        val alert = Alert(
            id = System.currentTimeMillis().toInt(),
            title = title,
            description = message,
            location = "Your area",
            severity = "Medium",
            timestamp = System.currentTimeMillis()
        )
        
        // Show a notification for this alert
        NotificationHelper.showAlertNotification(context, alert)
    }
}
