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

        val hourPicker =
            findViewById<NumberPicker>(R.id.hourPicker)

        val minutePicker =
            findViewById<NumberPicker>(R.id.minutePicker)

        val secondPicker =
            findViewById<NumberPicker>(R.id.secondPicker)

        val startButton =
            findViewById<Button>(R.id.startButton)

        // HOURS: 0–5
        hourPicker.minValue = 0
        hourPicker.maxValue = 5
        hourPicker.value = 0

        // MINUTES: 0–59
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = 25

        // SECONDS: 0–59
        secondPicker.minValue = 0
        secondPicker.maxValue = 59
        secondPicker.value = 0

        startButton.setOnClickListener {

            val hours =
                hourPicker.value

            val minutes =
                minutePicker.value

            val seconds =
                secondPicker.value

            if (
                hours == 0 &&
                minutes == 0 &&
                seconds == 0
            ) {

                Toast.makeText(
                    this,
                    "Please set a timer longer than 0 seconds.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val totalMilliseconds =
                (hours * 60 * 60 * 1000L) +
                        (minutes * 60 * 1000L) +
                        (seconds * 1000L)

            val endTime =
                System.currentTimeMillis() +
                        totalMilliseconds

            val prefs =
                getSharedPreferences(
                    "focus_prefs",
                    MODE_PRIVATE
                )

            prefs.edit()
                .putLong(
                    "focus_end_time",
                    endTime
                )
                .apply()

            // Start blocking
            val serviceIntent =
                Intent(
                    this,
                    FocusMonitorService::class.java
                )

            startForegroundService(
                serviceIntent
            )

            Toast.makeText(
                this,
                "Focus session started!",
                Toast.LENGTH_SHORT
            ).show()

            /*
             * Return to the ORIGINAL MainActivity.
             *
             * CLEAR_TOP removes TimerActivity,
             * TimerChoiceActivity, etc.
             *
             * SINGLE_TOP prevents Android from
             * creating a second MainActivity.
             */
            val home =
                Intent(
                    this,
                    MainActivity::class.java
                )

            home.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(home)

            finish()
        }
    }
}