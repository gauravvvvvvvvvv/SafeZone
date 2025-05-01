package com.example.safezone

import android.app.Application
import com.example.safezone.utils.NotificationHelper
import com.example.safezone.utils.SchedulingUtils

class SafeZoneApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize notification channels
        NotificationHelper.createNotificationChannels(this)
        
        // Schedule periodic safety checks
        SchedulingUtils.scheduleSafetyChecks(this)
    }
}
