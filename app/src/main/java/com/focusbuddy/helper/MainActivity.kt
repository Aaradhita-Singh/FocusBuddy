package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var resetSelectionsButton: Button

    private fun resetSavedApps() {
        val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)
        val savedApps = prefs.getStringSet("blocked_apps", emptySet())

        if (savedApps.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "No apps are currently selected.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 1. Clear saved apps
        prefs.edit().remove("blocked_apps").apply()

        // 2. STOP THE SERVICE 🔥
        stopService(Intent(this, FocusMonitorService::class.java))



        Toast.makeText(
            this,
            "Blocked apps have been reset.",
            Toast.LENGTH_SHORT
        ).show()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resetSelectionsButton = findViewById(R.id.resetSelectionsButton)
        resetSelectionsButton.setOnClickListener {
            resetSavedApps()
        }

        val chooseAppsButton = findViewById<Button>(R.id.chooseAppsButton)

        chooseAppsButton.setOnClickListener {
            startActivity(Intent(this, AppListActivity::class.java))
        }
    }
}



