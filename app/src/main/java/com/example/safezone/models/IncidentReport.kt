package com.example.safezone.models

import java.util.*

data class IncidentReport(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val location: String,
    val timestamp: Long,
    val safetyRating: Int,
    val imageUri: String? = null
) {
    // Convert to a formatted string for storage
    fun toStorageString(): String {
        return "ID: $id\n" +
               "Title: $title\n" +
               "Description: $description\n" +
               "Location: $location\n" +
               "Date/Time: ${Date(timestamp)}\n" +
               "Safety Rating: $safetyRating/5\n" +
               (if (imageUri != null) "Image: $imageUri" else "")
    }
}
