package com.example.safezone.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import android.widget.ProgressBar
import com.example.safezone.CustomRatingActivity
import com.example.safezone.R
import com.example.safezone.models.Alert
import com.example.safezone.utils.NotificationHelper
import com.example.safezone.utils.PreferenceHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private var prefsListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rootView = view
        
        // Set up emergency button
        val emergencyBtn = view.findViewById<Button>(R.id.btn_emergency)
        emergencyBtn.setOnClickListener {
            showEmergencyDialog()
        }
          // Update safety level from preferences
        val safetyLevel = PreferenceHelper.getSafetyLevel(requireContext())
        updateSafetyStatus(view, safetyLevel)
        
        // Add button to update safety rating
        val updateSafetyButton = Button(requireContext())
        updateSafetyButton.text = "Update Safety Rating"
        updateSafetyButton.setOnClickListener {
            val intent = Intent(requireContext(), CustomRatingActivity::class.java)
            startActivity(intent)
        }
        
        // Find the layout to add our button to
        val safetyCardLayout = view.findViewById<androidx.cardview.widget.CardView>(R.id.card_safety_status)
        if (safetyCardLayout != null) {
            // Get the first child LinearLayout inside the CardView
            val linearLayout = safetyCardLayout.getChildAt(0) as? LinearLayout
            if (linearLayout != null) {
                updateSafetyButton.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    topMargin = resources.getDimensionPixelSize(R.dimen.margin_medium)
                }
                linearLayout.addView(updateSafetyButton)
            }
        }
        
        // Set up community chat FAB
        val communityFab = view.findViewById<FloatingActionButton>(R.id.fab_community_chat)
        communityFab.setOnClickListener {
            // Navigate to CommunityFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.example.safezone.fragments.CommunityFragment())
                .addToBackStack(null)
                .commit()
        }
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        val prefs = requireContext().getSharedPreferences("safezone_user_prefs", 0)
        prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "safety_level") {
                rootView?.let {
                    val newLevel = PreferenceHelper.getSafetyLevel(requireContext())
                    updateSafetyStatus(it, newLevel)
                }
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
        // Also update immediately in case of changes while away
        rootView?.let {
            val newLevel = PreferenceHelper.getSafetyLevel(requireContext())
            updateSafetyStatus(it, newLevel)
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = requireContext().getSharedPreferences("safezone_user_prefs", 0)
        prefsListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
    }
    
    private fun updateSafetyStatus(view: View, safetyLevel: Int) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_safety)
        val safetyText = view.findViewById<TextView>(R.id.text_safety_level)
        
        // Update progress bar
        progressBar.progress = safetyLevel
        
        // Update safety text based on level
        val statusText = when {
            safetyLevel >= 75 -> "Safety Level: Good"
            safetyLevel >= 50 -> "Safety Level: Moderate"
            else -> "Safety Level: Caution"
        }
        
        safetyText.text = statusText
        
        // Set color based on safety level
        val textColor = when {
            safetyLevel >= 75 -> R.color.safe
            safetyLevel >= 50 -> R.color.warning
            else -> R.color.danger
        }
        safetyText.setTextColor(requireContext().getColor(textColor))
    }
    
    private fun showEmergencyDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Emergency")
                .setMessage("Do you want to send an emergency alert to your contacts?")
                .setPositiveButton("Yes") { _, _ ->
                    // Create and show emergency alert notification
                    sendEmergencyAlert()
                    Toast.makeText(context, "Emergency alert sent!", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }
    
    private fun sendEmergencyAlert() {
        // Get user's name from preferences
        val userName = PreferenceHelper.getUserName(requireContext())
        val name = if (userName.isBlank()) "A user" else userName
        
        // Create an emergency alert
        val alert = Alert(
            id = System.currentTimeMillis().toInt(),
            title = "EMERGENCY ALERT",
            description = "$name has triggered an emergency alert. They may need immediate assistance.",
            location = "User's last known location",
            severity = "High",
            timestamp = System.currentTimeMillis()
        )
        
        // Show notification
        NotificationHelper.showAlertNotification(requireContext(), alert)
    }
}
