package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TimerChoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_timer_choice)

        val yesButton = findViewById<Button>(R.id.yesButton)
        val noButton = findViewById<Button>(R.id.noButton)

        yesButton.setOnClickListener {

            val intent = Intent(
                this,
                TimerActivity::class.java
            )

            startActivity(intent)
            finish()
        }

        noButton.setOnClickListener {

            startFocusSession()
            finish()
        }
    }

    private fun startFocusSession() {

        val intent = Intent(
            this,
            FocusMonitorService::class.java
        )

        startForegroundService(intent)
    }
}