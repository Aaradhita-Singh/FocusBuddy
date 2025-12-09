package com.focusbuddy.helper

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import android.util.Log

class FocusAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            val pkg = event.packageName?.toString() ?: return
            Log.d("FOCUS_ACC", "Detected foreground: $pkg")

            val blockedApps = FocusSettings.blockedApps

            if (pkg in blockedApps) {
                Log.d("FOCUS_ACC", "BLOCKED → redirecting")

                val intent = Intent(this, BlockedActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {}
}
