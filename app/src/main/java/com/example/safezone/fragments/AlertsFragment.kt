package com.example.safezone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safezone.R
import com.example.safezone.adapters.AlertAdapter
import com.example.safezone.models.Alert
import com.example.safezone.utils.DatabaseHelper

class AlertsFragment : Fragment() {

    private lateinit var alertsRecyclerView: RecyclerView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)
        
        alertsRecyclerView = view.findViewById(R.id.recycler_alerts)
        setupRecyclerView()
        
        return view
    }
      private fun setupRecyclerView() {
        // Get alerts from database
        val dbHelper = DatabaseHelper(requireContext())
        var alerts = dbHelper.getAllAlerts()
        
        // If there are no alerts in the database, create some sample ones
        if (alerts.isEmpty()) {
            val sampleAlerts = listOf(
                Alert(1, "Suspicious activity", "Someone following people at night", "Main Street", "High", System.currentTimeMillis()),
                Alert(2, "Road closed", "Accident on Highway 101", "Highway 101", "Medium", System.currentTimeMillis() - 3600000),
                Alert(3, "Power outage", "Expected to be fixed in 2 hours", "Downtown area", "Medium", System.currentTimeMillis() - 7200000)
            )
            
            // Insert sample alerts into the database
            for (alert in sampleAlerts) {
                dbHelper.insertAlert(alert)
            }
            
            // Retrieve the inserted alerts
            alerts = dbHelper.getAllAlerts()
        }
        
        // Set up the RecyclerView
        alertsRecyclerView.layoutManager = LinearLayoutManager(context)
        alertsRecyclerView.adapter = AlertAdapter(alerts) { alert ->
            // Show alert details dialog
            showAlertDetailsDialog(alert)
        }
    }
    
    private fun showAlertDetailsDialog(alert: Alert) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(alert.title)
            .setMessage("Location: ${alert.location}\n\n${alert.description}")
            .setPositiveButton("OK", null)
            
        if (alert.severity.equals("high", ignoreCase = true)) {
            builder.setIcon(android.R.drawable.ic_dialog_alert)
        }
            
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }
}
