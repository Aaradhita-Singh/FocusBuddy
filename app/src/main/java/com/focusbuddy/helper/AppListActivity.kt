package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import android.os.Build

class AppListActivity : AppCompatActivity() {
    private fun saveSelectedApps(apps: List<String>) {
        val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)
        prefs.edit()
            .putStringSet("blocked apps", apps.toSet())
            .apply()
    }

    private fun startFocusService(apps: List<String>) {
        val intent = Intent(this, FocusMonitorService::class.java)
        intent.putStringArrayListExtra("blockedApps", ArrayList(apps))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent)}
        else {
            startService(intent)
        }
    }


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
            if (selectedApps.isNotEmpty()) {
                saveSelectedApps(selectedApps)
                startFocusService(selectedApps)
            }

        }
    }
}
