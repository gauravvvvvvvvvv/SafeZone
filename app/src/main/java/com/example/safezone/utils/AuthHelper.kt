package com.example.safezone.utils

import android.content.Context
import com.example.safezone.models.User
import com.google.gson.Gson

object AuthHelper {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER = "user_data"
    private const val KEY_LOGGED_IN = "logged_in"

    fun saveUser(context: Context, user: User) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER, Gson().toJson(user)).apply()
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
    }

    fun getUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_USER, null)
        return if (json != null) Gson().fromJson(json, User::class.java) else null
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
