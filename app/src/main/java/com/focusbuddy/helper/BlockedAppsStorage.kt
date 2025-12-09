package com.focusbuddy.helper

import android.content.Context

object BlockedAppsStorage {

    private const val PREF_NAME = "blocked_apps_pref"
    private const val KEY_BLOCKED = "blocked_list"

    fun saveBlockedApps(context: Context, apps: List<String>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_BLOCKED, apps.toSet()).apply()
    }

    fun getBlockedApps(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_BLOCKED, emptySet())!!.toList()
    }
}
