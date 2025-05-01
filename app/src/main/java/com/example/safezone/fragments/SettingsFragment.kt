package com.example.safezone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.safezone.MainActivity
import com.example.safezone.R
import com.example.safezone.utils.AuthHelper
import com.example.safezone.utils.NotificationUtils
import com.example.safezone.utils.PreferenceHelper
import com.example.safezone.utils.SchedulingUtils

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        // Setup name preference
        setupNamePreference()
        
        // Setup notification preference
        setupNotificationPreference()
        
        // Setup emergency contact preferences
        setupEmergencyContactPreferences()

        // Add logout preference
        val logoutPref = Preference(requireContext())
        logoutPref.key = "logout"
        logoutPref.title = "Log Out"
        logoutPref.summary = "Sign out of your account"
        logoutPref.icon = resources.getDrawable(android.R.drawable.ic_lock_power_off, null)
        preferenceScreen.addPreference(logoutPref)
        logoutPref.setOnPreferenceClickListener {
            // Clear login state for XML-based auth
            val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putBoolean("logged_in", false).apply()
            val intent = Intent(requireContext(), com.example.safezone.AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            true
        }
    }
    
    private fun setupNamePreference() {
        val namePref = findPreference<EditTextPreference>("user_name")
        namePref?.apply {
            val savedName = PreferenceHelper.getUserName(requireContext())
            if (savedName.isNotEmpty()) {
                summary = savedName
            }
            
            setOnPreferenceChangeListener { _, newValue ->
                val name = newValue as String
                summary = name
                PreferenceHelper.saveUserProfile(
                    requireContext(),
                    name,
                    PreferenceHelper.getUserPhone(requireContext()),
                    PreferenceHelper.getUserEmail(requireContext())
                )
                true
            }
        }
    }
    
    private fun setupNotificationPreference() {
        val notificationPref = findPreference<SwitchPreferenceCompat>("enable_notifications")
        notificationPref?.apply {
            isChecked = PreferenceHelper.areNotificationsEnabled(requireContext())
            
            setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                PreferenceHelper.setNotificationsEnabled(requireContext(), enabled)
                
                // If enabling notifications but they're disabled at system level
                if (enabled && !NotificationUtils.areNotificationsEnabled(requireContext())) {
                    NotificationUtils.openNotificationSettings(requireContext())
                }
                
                // Toggle scheduling of safety checks based on preference
                if (enabled) {
                    SchedulingUtils.scheduleSafetyChecks(requireContext())
                } else {
                    SchedulingUtils.cancelSafetyChecks(requireContext())
                }
                
                true
            }
        }
    }
    
    private fun setupEmergencyContactPreferences() {
        // Setup phone preference
        val phonePref = findPreference<EditTextPreference>("emergency_phone")
        phonePref?.apply {
            val savedPhone = PreferenceHelper.getUserPhone(requireContext())
            if (savedPhone.isNotEmpty()) {
                summary = savedPhone
            }
            
            setOnPreferenceChangeListener { _, newValue ->
                val phone = newValue as String
                summary = phone
                PreferenceHelper.saveUserProfile(
                    requireContext(),
                    PreferenceHelper.getUserName(requireContext()),
                    phone,
                    PreferenceHelper.getUserEmail(requireContext())
                )
                true
            }
        }
        
        // Setup email preference
        val emailPref = findPreference<EditTextPreference>("emergency_email")
        emailPref?.apply {
            val savedEmail = PreferenceHelper.getUserEmail(requireContext())
            if (savedEmail.isNotEmpty()) {
                summary = savedEmail
            }
            
            setOnPreferenceChangeListener { _, newValue ->
                val email = newValue as String
                summary = email
                PreferenceHelper.saveUserProfile(
                    requireContext(),
                    PreferenceHelper.getUserName(requireContext()),
                    PreferenceHelper.getUserPhone(requireContext()),
                    email
                )
                true
            }
        }
    }
}
