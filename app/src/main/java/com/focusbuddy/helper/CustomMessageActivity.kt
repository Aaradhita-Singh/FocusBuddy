package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CustomMessageActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var messageInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_message)

        saveButton = findViewById(R.id.saveStartButton)
        messageInput = findViewById(R.id.messageInput)

        saveButton.setOnClickListener {
            val message = messageInput.text.toString().trim()

            // GET THE APPS SELECTED FROM PREVIOUS SCREEN
            val blockedApps = SelectedAppsHolder.selected

            // PREVENT EMPTY APP LIST
            if (blockedApps.isEmpty()) {
                finish()
                return@setOnClickListener
            }

            // START SERVICE & SEND DATA
            val serviceIntent = Intent(this, FocusMonitorService::class.java).apply {
                putStringArrayListExtra("blockedApps", ArrayList(blockedApps))
                putExtra("message", message)
            }

            startForegroundService(serviceIntent)

            // GO BACK TO MAIN SCREEN
            val home = Intent(this, MainActivity::class.java)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(home)
            finish()
        }
    }
}
