package com.example.safezone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safezone.R
import com.example.safezone.models.Alert
import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter(
    private val alerts: List<Alert>,
    private val onAlertClick: (Alert) -> Unit
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.bind(alert)
        holder.itemView.setOnClickListener { onAlertClick(alert) }
    }

    override fun getItemCount(): Int = alerts.size

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_alert_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_alert_description)
        private val locationTextView: TextView = itemView.findViewById(R.id.text_alert_location)
        private val timeTextView: TextView = itemView.findViewById(R.id.text_alert_time)
        private val severityTextView: TextView = itemView.findViewById(R.id.text_alert_severity)

        fun bind(alert: Alert) {
            titleTextView.text = alert.title
            descriptionTextView.text = alert.description
            locationTextView.text = alert.location
            
            // Format timestamp
            val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            timeTextView.text = dateFormat.format(Date(alert.timestamp))
            
            // Set severity text and color
            severityTextView.text = "Severity: ${alert.severity}"
            when (alert.severity.lowercase()) {
                "high" -> severityTextView.setTextColor(itemView.context.getColor(R.color.danger))
                "medium" -> severityTextView.setTextColor(itemView.context.getColor(R.color.warning))
                else -> severityTextView.setTextColor(itemView.context.getColor(R.color.safe))
            }
        }
    }
}
