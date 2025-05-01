package com.example.safezone.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.safezone.utils.SchedulingUtils

/**
 * Receiver that gets triggered when device boot is completed
 * Used to restore scheduled alarms and work requests
 */
class BootCompletedReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Restore any scheduled alarms or work requests
            SchedulingUtils.scheduleSafetyChecks(context)
            
            // In a real app, you would also reschedule any user-specific alarms 
            // from a database or shared preferences
        }
    }
}
