package com.example.safezone

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safezone.utils.PreferenceHelper
import com.example.safezone.views.SafetyRatingView

class CustomRatingActivity : AppCompatActivity() {

    private lateinit var safetyRatingView: SafetyRatingView
    private lateinit var resultText: TextView
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_custom_rating)
        
        // Initialize views
        safetyRatingView = findViewById(R.id.safety_rating_view)
        resultText = findViewById(R.id.text_result)
        submitButton = findViewById(R.id.btn_submit_rating)
        
        // Set up rating change listener
        safetyRatingView.setOnRatingChangeListener { rating ->
            when (rating) {
                1 -> resultText.text = "Selected rating: Danger"
                2 -> resultText.text = "Selected rating: Caution"
                3 -> resultText.text = "Selected rating: Safe"
                else -> resultText.text = "Selected rating: None"
            }
        }
        
        // Set up submit button
        submitButton.setOnClickListener {
            val rating = safetyRatingView.getRating()
            if (rating == 0) {
                Toast.makeText(this, "Please select a safety rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Convert rating (1-3) to safety level percentage (0-100)
            val safetyLevel = when (rating) {
                1 -> 25  // Danger
                2 -> 60  // Caution
                3 -> 95  // Safe
                else -> 50 // Default
            }
            
            // Save safety level to preferences
            PreferenceHelper.setSafetyLevel(this, safetyLevel)
            
            Toast.makeText(this, "Safety rating submitted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
