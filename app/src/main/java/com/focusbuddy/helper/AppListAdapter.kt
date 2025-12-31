package com.focusbuddy.helper

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView

class AppListAdapter(private val context: Context) : BaseAdapter() {

    private val pm = context.packageManager

    // Show all launchable apps
    private val apps: List<ApplicationInfo> =
        pm.getInstalledApplications(0).filter {
            pm.getLaunchIntentForPackage(it.packageName) != null
        }

    // Store selection state safely
    private val selected = mutableSetOf<String>()

    override fun getCount(): Int = apps.size
    override fun getItem(position: Int): Any = apps[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_app, parent, false)

        val icon = view.findViewById<ImageView>(R.id.appIcon)
        val name = view.findViewById<TextView>(R.id.appName)
        val checkBox = view.findViewById<CheckBox>(R.id.appCheckBox)

        val app = apps[position]

        // Set icon + name
        icon.setImageDrawable(pm.getApplicationIcon(app.packageName))
        name.text = pm.getApplicationLabel(app)
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = selected.contains(app.packageName)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selected.add(app.packageName)
            } else {
                selected.remove(app.packageName)
            }
        }

        return view
    }

    fun getSelectedApps(): List<String> = selected.toList()
}
