package com.focusbuddy.helper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class FocusBlockerService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("FOCUS_DEBUG", "AccessibilityService CONNECTED")

        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = event.packageName?.toString() ?: return

        // Check if package is blocked
        if (pkg in FocusSettings.blockedApps) {
            Log.d("FOCUS_DEBUG", "BLOCKING app via Accessibility: $pkg")

            // Redirect user to BlockedActivity
            val intent = Intent(this, BlockedActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            intent.putExtra("message", FocusSettings.customMessage)
            startActivity(intent)
        }
    }

    override fun onInterrupt() {}
}
