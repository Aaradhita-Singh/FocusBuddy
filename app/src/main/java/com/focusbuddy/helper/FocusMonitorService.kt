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

    private fun sendTimerFinishedNotification() {
        val channelId = "focus_timer_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Focus Timer",
                NotificationManager.IMPORTANCE_HIGH
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Great job!")
            .setContentText("Your timer is finished. Your selected apps are unlocked.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(NotificationManager::class.java)

        notificationManager.notify(2, notification)
    }

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
                val prefs = getSharedPreferences(
                    "focus_prefs",
                    MODE_PRIVATE
                )

                val blockedApps =
                    prefs.getStringSet(
                        "blocked_apps",
                        emptySet()
                    ) ?: emptySet()

                if (blockedApps.isEmpty()) {
                    Log.d(
                        "FOCUS_DEBUG",
                        "No blocked apps — stopping service"
                    )

                    stopSelf()
                    return
                }

// Check timer
                val endTime =
                    prefs.getLong(
                        "focus_end_time",
                        0L
                    )

                if (
                    endTime > 0 &&
                    System.currentTimeMillis() >= endTime
                ) {

                    Log.d(
                        "FOCUS_DEBUG",
                        "Focus timer finished!"
                    )

                    // Unlock apps
                    prefs.edit()
                        .remove("blocked_apps")
                        .remove("focus_end_time")
                        .apply()

                    // Tell the user
                    sendTimerFinishedNotification()

                    stopSelf()
                    return
                }

                checkForegroundApp()

                handler.postDelayed(this, 700)
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
