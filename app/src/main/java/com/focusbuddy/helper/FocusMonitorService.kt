package com.focusbuddy.helper

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log

class FocusMonitorService : Service() {

    private lateinit var usageStats: UsageStatsManager
    private val handler = Handler(Looper.getMainLooper())
    private var monitorRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("FOCUS_DEBUG", "Service CREATED")

        usageStats = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        startForegroundServiceNotification()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FOCUS_DEBUG", "onStartCommand CALLED")
        startMonitoringLoop()
        return START_NOT_STICKY
    }

    private fun startMonitoringLoop() {
        Log.d("FOCUS_DEBUG", "Monitoring loop started")

        monitorRunnable = object : Runnable {
            override fun run() {

                val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)
                val blockedApps =
                    prefs.getStringSet("blocked_apps", emptySet()) ?: emptySet()

                // ✅ If no apps are blocked, STOP EVERYTHING
                if (blockedApps.isEmpty()) {
                    Log.d("FOCUS_DEBUG", "No blocked apps — stopping service")
                    stopSelf()
                    return
                }

                checkForegroundApp()
                handler.postDelayed(this, 700)
            }
        }

        handler.post(monitorRunnable!!)
    }

    private fun checkForegroundApp() {
        val current = getForegroundApp()
        Log.d("FOCUS_DEBUG", "Foreground app detected: $current")
        // AccessibilityService handles blocking
    }

    private fun getForegroundApp(): String? {
        try {
            val end = System.currentTimeMillis()
            val begin = end - 2000

            val events = usageStats.queryEvents(begin, end)
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FOCUS_DEBUG", "Service DESTROYED")

        monitorRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun startForegroundServiceNotification() {
        val channelId = "focus_monitor_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Focus Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("FocusBuddy is monitoring apps")
            .setContentText("Blocking distractions")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .build()

        startForeground(1, notification)
    }
}
