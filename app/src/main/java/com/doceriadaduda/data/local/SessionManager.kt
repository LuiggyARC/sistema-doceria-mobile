package com.doceriadaduda.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_session", Context.MODE_PRIVATE)

    var companyId: Int
        get() = prefs.getInt("current_company_id", -1)
        set(value) = prefs.edit().putInt("current_company_id", value).apply()

    var companyName: String?
        get() = prefs.getString("current_company_name", null)
        set(value) = prefs.edit().putString("current_company_name", value).apply()

    var isAdmin: Boolean
        get() = prefs.getBoolean("is_admin", false)
        set(value) = prefs.edit().putBoolean("is_admin", value).apply()

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = companyId != -1
}
