package com.example.safezone.models

data class Alert(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val severity: String,
    val timestamp: Long
)
