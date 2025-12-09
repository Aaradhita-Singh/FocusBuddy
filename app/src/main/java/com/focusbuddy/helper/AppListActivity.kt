package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
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

            if (selectedApps.isNotEmpty()) {
                SelectedAppsHolder.selected = selectedApps
                startActivity(Intent(this, CustomMessageActivity::class.java))
            }
        }
    }
}
