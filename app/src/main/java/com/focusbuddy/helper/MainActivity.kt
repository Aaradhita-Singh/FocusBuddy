package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chooseAppsButton = findViewById<Button>(R.id.chooseAppsButton)

        chooseAppsButton.setOnClickListener {
            startActivity(Intent(this, AppListActivity::class.java))
        }
    }
}



