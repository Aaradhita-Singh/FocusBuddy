package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_timer)

        val minutePicker =
            findViewById<NumberPicker>(R.id.minutePicker)

        val startButton =
            findViewById<Button>(R.id.startButton)

        minutePicker.minValue = 1
        minutePicker.maxValue = 180
        minutePicker.value = 25

        startButton.setOnClickListener {

            val minutes = minutePicker.value

            val prefs = getSharedPreferences(
                "focus_prefs",
                MODE_PRIVATE
            )

            val endTime =
                System.currentTimeMillis() +
                        (minutes * 60 * 1000L)

            prefs.edit()
                .putLong("focus_end_time", endTime)
                .apply()

            // Start monitoring
            val serviceIntent = Intent(
                this,
                FocusMonitorService::class.java
            )

            startForegroundService(serviceIntent)

            Toast.makeText(
                this,
                "Focus session started!",
                Toast.LENGTH_SHORT
            ).show()

            val home = Intent(
                this,
                MainActivity::class.java
            )

            home.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(home)
            finish()
        }
    }
}