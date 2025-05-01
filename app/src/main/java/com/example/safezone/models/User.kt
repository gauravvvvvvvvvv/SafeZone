package com.example.safezone.models

data class User(
    val name: String,
    val email: String,
    val emergencyContacts: List<String>,
    val password: String // For demo only; in production, use secure storage
)
