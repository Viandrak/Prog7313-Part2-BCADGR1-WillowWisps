package com.example.willowwallet.utils
import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("willow_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val NO_USER = -1
    }

    fun saveSession(userId: Int, username: String, displayName: String) {
        prefs.edit().putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_DISPLAY_NAME, displayName).apply()
    }

    fun clearSession() = prefs.edit().clear().apply()
    fun isLoggedIn() = prefs.getInt(KEY_USER_ID, NO_USER) != NO_USER
    fun getUserId() = prefs.getInt(KEY_USER_ID, NO_USER)
    fun getUsername() = prefs.getString(KEY_USERNAME, "") ?: ""
    fun getDisplayName() = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""
}