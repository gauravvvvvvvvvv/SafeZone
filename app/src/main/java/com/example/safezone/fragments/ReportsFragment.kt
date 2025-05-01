package com.example.safezone.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.safezone.R
import com.example.safezone.models.IncidentReport
import com.example.safezone.utils.DatabaseHelper
import com.example.safezone.utils.FileStorageUtils
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {
    
    private lateinit var datePickerBtn: Button
    private lateinit var timePickerBtn: Button
    private lateinit var safetyRatingBar: RatingBar
    private lateinit var descriptionEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var selectedDateTimeText: TextView
    private lateinit var addPhotoButton: Button
    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        
        // Initialize views
        datePickerBtn = view.findViewById(R.id.btn_pick_date)
        timePickerBtn = view.findViewById(R.id.btn_pick_time)
        safetyRatingBar = view.findViewById(R.id.rating_safety)
        descriptionEditText = view.findViewById(R.id.edit_description)
        locationEditText = view.findViewById(R.id.edit_location)
        submitButton = view.findViewById(R.id.btn_submit)
        selectedDateTimeText = view.findViewById(R.id.text_selected_datetime)
        addPhotoButton = view.findViewById(R.id.btn_add_photo)
        val incidentTypeSpinner = view.findViewById<Spinner>(R.id.spinner_incident_type)
        val incidentTypes = listOf(
            "Select type",
            "Theft/Burglary",
            "Vandalism",
            "Suspicious Activity",
            "Medical Emergency",
            "Fire/Smoke",
            "Accident",
            "Natural Disaster",
            "Harassment",
            "Community Chat",
            "Other"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, incidentTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        incidentTypeSpinner.adapter = adapter
        
        // Set current date and time
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        selectedDateTimeText.text = "Date: ${dateFormat.format(calendar.time)} | Time: ${timeFormat.format(calendar.time)}"
        
        // Set up date picker
        datePickerBtn.setOnClickListener {
            showDatePicker()
        }
        
        // Set up time picker
        timePickerBtn.setOnClickListener {
            showTimePicker()
        }
          // Set up submit button
        submitButton.setOnClickListener {
            val title = "Incident Report"
            val description = descriptionEditText.text.toString()
            val location = locationEditText.text.toString()
            val rating = safetyRatingBar.rating.toInt()
            
            if (description.isBlank() || location.isBlank()) {
                showCustomToast("Please fill in all fields")
                return@setOnClickListener
            }
            
            // Create report object
            val report = IncidentReport(
                title = title,
                description = description,
                location = location,
                timestamp = System.currentTimeMillis(),
                safetyRating = rating,
                imageUri = selectedImageUri?.toString() ?: ""
            )
            
            // Save report to database
            val dbHelper = DatabaseHelper(requireContext())
            dbHelper.insertReport(report)

            // Also add an alert for this report
            val severity = when (rating) {
                1 -> "High"
                2 -> "Medium"
                3, 4, 5 -> "Low"
                else -> "Medium"
            }
            val alert = com.example.safezone.models.Alert(
                id = System.currentTimeMillis().toInt(),
                title = title,
                description = description,
                location = location,
                severity = severity,
                timestamp = System.currentTimeMillis()
            )
            dbHelper.insertAlert(alert)
            
            // Also save to internal storage as a text file
            val reportSaved = FileStorageUtils.saveReport(
                requireContext(),
                title,
                report.toStorageString()
            )
            
            if (reportSaved) {
                showCustomToast("Report submitted successfully!")
                clearForm()
            } else {
                showCustomToast("Error saving report to storage")
            }
        }
        
        addPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
        
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            showCustomToast("Image selected!")
        }
    }
    
    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.setOnDateSetListener { year, month, day ->
            // Update the date display
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)
            
            // Extract the current time part from the text
            val currentText = selectedDateTimeText.text.toString()
            val timePart = if (currentText.contains("|")) {
                currentText.split("|")[1].trim()
            } else {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Time: ${timeFormat.format(Calendar.getInstance().time)}"
            }
            
            // Update the text with new date and existing time
            selectedDateTimeText.text = "Date: $formattedDate | $timePart"
        }
        datePickerFragment.show(parentFragmentManager, DatePickerFragment.TAG)
    }
    
    private fun showTimePicker() {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.setOnTimeSetListener { hourOfDay, minute ->
            // Update the time display
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val formattedTime = timeFormat.format(calendar.time)
            
            // Extract the current date part from the text
            val currentText = selectedDateTimeText.text.toString()
            val datePart = if (currentText.contains("|")) {
                currentText.split("|")[0].trim()
            } else {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                "Date: ${dateFormat.format(Calendar.getInstance().time)}"
            }
            
            // Update the text with existing date and new time
            selectedDateTimeText.text = "$datePart | Time: $formattedTime"
        }
        timePickerFragment.show(parentFragmentManager, TimePickerFragment.TAG)
    }
    
    private fun clearForm() {
        descriptionEditText.text.clear()
        locationEditText.text.clear()
        safetyRatingBar.rating = 0f
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        selectedDateTimeText.text = "Date: ${dateFormat.format(calendar.time)} | Time: ${timeFormat.format(calendar.time)}"
    }
    
    private fun showCustomToast(message: String) {
        context?.let { 
            android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
