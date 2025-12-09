package com.focusbuddy.helper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.FrameLayout

class BlockedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // simple blocker UI
        val text = TextView(this).apply {
            text = "This app is blocked by FocusBuddy"
            textSize = 24f
            setPadding(40, 40, 40, 40)
        }

        val layout = FrameLayout(this)
        layout.addView(text)
        setContentView(layout)
    }
}
