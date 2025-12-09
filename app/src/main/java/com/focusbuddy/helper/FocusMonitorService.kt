package com.focusbuddy.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class FocusMonitorService : Service() {

    private lateinit var usageStats: UsageStatsManager
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        Log.d("FOCUS_DEBUG", "Service CREATED")

        usageStats = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        startForegroundServiceNotification()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundServiceNotification() {
        Log.d("FOCUS_DEBUG", "Starting foreground notification")

        val channelId = "focus_monitor_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Focus Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("FocusBuddy is monitoring apps")
            .setContentText("Blocking distractions")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FOCUS_DEBUG", "onStartCommand CALLED")

        // 🔥 Store in global settings instead of local variables
        FocusSettings.blockedApps =
            intent?.getStringArrayListExtra("blockedApps") ?: emptyList()

        FocusSettings.customMessage =
            intent?.getStringExtra("message") ?: "Stay focused!"

        Log.d("FOCUS_DEBUG", "Blocked apps = ${FocusSettings.blockedApps}")
        Log.d("FOCUS_DEBUG", "Message = ${FocusSettings.customMessage}")

        startMonitoringLoop()

        return START_STICKY
    }

    private fun startMonitoringLoop() {
        Log.d("FOCUS_DEBUG", "Monitoring loop started")

        val runnable = object : Runnable {
            override fun run() {
                checkForegroundApp()
                handler.postDelayed(this, 700)
            }
        }

        handler.post(runnable)
    }

    private fun checkForegroundApp() {
        val current = getForegroundApp()

        Log.d("FOCUS_DEBUG", "Foreground app detected: $current")
        // No launching — AccessibilityService handles blocking
    }

    private fun getForegroundApp(): String? {
        try {
            val end = System.currentTimeMillis()
            val begin = end - 2000

            val events = usageStats.queryEvents(begin, end) ?: return null
            val event = UsageEvents.Event()

            var lastPackage: String? = null

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    lastPackage = event.packageName
                }
            }

            return lastPackage
        } catch (e: Exception) {
            Log.e("FOCUS_DEBUG", "getForegroundApp FAILED: ${e.message}")
            return null
        }
    }

}
