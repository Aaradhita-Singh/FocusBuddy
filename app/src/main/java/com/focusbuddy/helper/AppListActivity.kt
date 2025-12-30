package com.focusbuddy.helper

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AppListActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var listView: ListView
    private lateinit var adapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        listView = findViewById(R.id.appListView)
        saveButton = findViewById(R.id.saveButton)
        adapter = AppListAdapter(this)

        listView.adapter = adapter

        saveButton.setOnClickListener {
            val selectedApps = adapter.getSelectedApps()

            if (selectedApps.isEmpty()) {
                Toast.makeText(this, "No apps selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1️⃣ Save apps correctly
            val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)
            prefs.edit()
                .putStringSet("blocked_apps", selectedApps.toSet())
                .apply()

            // 2️⃣ STOP old service first (CRITICAL)
            stopService(Intent(this, FocusMonitorService::class.java))

            // 3️⃣ Start service with fresh data
            val intent = Intent(this, FocusMonitorService::class.java)
            intent.putStringArrayListExtra(
                "blockedApps",
                ArrayList(selectedApps)
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }

            // 4️⃣ Feedback + return home
            Toast.makeText(this, "Apps blocked", Toast.LENGTH_SHORT).show()

            val home = Intent(this, MainActivity::class.java)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(home)
            finish()
        }
    }
}
