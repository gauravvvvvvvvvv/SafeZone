package com.example.safezone.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.safezone.models.Alert

/**
 * Helper class for managing shared preferences storage for the SafeZone app
 */
object PreferenceHelper {
    // Shared Preferences file names
    private const val SHARED_PREFS_USER = "safezone_user_prefs"
    private const val SHARED_PREFS_ALERTS = "safezone_alerts_prefs"
    
    // Keys
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_SAVED_ALERTS = "saved_alerts"
    private const val KEY_SAFETY_LEVEL = "safety_level"
    private const val KEY_FIRST_RUN = "first_run"
    
    // Get SharedPreferences
    private fun getUserPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_USER, Context.MODE_PRIVATE)
    }
    
    private fun getAlertPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_ALERTS, Context.MODE_PRIVATE)
    }
    
    // User profile methods
    fun saveUserProfile(context: Context, name: String, phone: String, email: String) {
        getUserPrefs(context).edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }
    
    fun getUserName(context: Context): String {
        return getUserPrefs(context).getString(KEY_USER_NAME, "") ?: ""
    }
    
    fun getUserPhone(context: Context): String {
        return getUserPrefs(context).getString(KEY_USER_PHONE, "") ?: ""
    }
    
    fun getUserEmail(context: Context): String {
        return getUserPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    }
    
    // Notification settings
    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        getUserPrefs(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
    
    fun areNotificationsEnabled(context: Context): Boolean {
        return getUserPrefs(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
    
    // Alerts storage methods
    fun saveAlerts(context: Context, alerts: List<Alert>) {
        val json = Gson().toJson(alerts)
        getAlertPrefs(context).edit().putString(KEY_SAVED_ALERTS, json).apply()
    }
    
    fun getSavedAlerts(context: Context): List<Alert> {
        val json = getAlertPrefs(context).getString(KEY_SAVED_ALERTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Alert>>() {}.type
        return Gson().fromJson(json, type)
    }
    
    fun addAlert(context: Context, alert: Alert) {
        val alerts = getSavedAlerts(context).toMutableList()
        alerts.add(0, alert) // Add to beginning of list
        saveAlerts(context, alerts)
    }
    
    // Safety level methods
    fun setSafetyLevel(context: Context, level: Int) {
        getUserPrefs(context).edit().putInt(KEY_SAFETY_LEVEL, level).apply()
    }
    
    fun getSafetyLevel(context: Context): Int {
        return getUserPrefs(context).getInt(KEY_SAFETY_LEVEL, 75) // Default to 75% safety
    }
    
    // First run check
    fun isFirstRun(context: Context): Boolean {
        val isFirstRun = getUserPrefs(context).getBoolean(KEY_FIRST_RUN, true)
        if (isFirstRun) {
            // Mark as no longer first run
            getUserPrefs(context).edit().putBoolean(KEY_FIRST_RUN, false).apply()
        }
        return isFirstRun
    }
}
