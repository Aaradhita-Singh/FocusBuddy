package com.focusbuddy.helper

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import android.util.Log

class FocusAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)
        val blockedApps =
            prefs.getStringSet("blocked_apps", emptySet()) ?: emptySet()

        if (blockedApps.contains(packageName)) {
            launchBlockScreen()
        }
    }
    private fun launchBlockScreen() {
        val intent = Intent(this, BlockedActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }



    override fun onInterrupt() {}
}
