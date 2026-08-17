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

    private val prefs =
        context.getSharedPreferences(
            "focus_prefs",
            Context.MODE_PRIVATE
        )

    // Apps that are currently selected
    private val selected =
        prefs.getStringSet(
            "blocked_apps",
            emptySet()
        )?.toMutableSet() ?: mutableSetOf()

    // All launchable apps
    private val allApps: List<ApplicationInfo> =
        pm.getInstalledApplications(0)
            .filter {
                pm.getLaunchIntentForPackage(it.packageName) != null &&
                        it.packageName != context.packageName
            }
            .sortedBy {
                pm.getApplicationLabel(it)
                    .toString()
                    .lowercase()
            }

    // Apps currently being displayed
    private var displayedApps: List<ApplicationInfo> =
        allApps

    override fun getCount(): Int =
        displayedApps.size

    override fun getItem(position: Int): Any =
        displayedApps[position]

    override fun getItemId(position: Int): Long =
        position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val view =
            convertView ?: LayoutInflater.from(context)
                .inflate(
                    R.layout.item_app,
                    parent,
                    false
                )

        val icon =
            view.findViewById<ImageView>(
                R.id.appIcon
            )

        val name =
            view.findViewById<TextView>(
                R.id.appName
            )

        val checkBox =
            view.findViewById<CheckBox>(
                R.id.appCheckBox
            )

        val app =
            displayedApps[position]

        icon.setImageDrawable(
            pm.getApplicationIcon(
                app.packageName
            )
        )

        name.text =
            pm.getApplicationLabel(app)

        // Prevent recycled ListView rows from
        // accidentally changing another checkbox.
        checkBox.setOnCheckedChangeListener(null)

        checkBox.isChecked =
            selected.contains(
                app.packageName
            )

        checkBox.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                selected.add(
                    app.packageName
                )
            } else {
                selected.remove(
                    app.packageName
                )
            }
        }

        return view
    }

    fun filterApps(query: String) {

        val search =
            query.trim().lowercase()

        displayedApps =
            if (search.isEmpty()) {

                allApps

            } else {

                allApps.filter { app ->

                    pm.getApplicationLabel(app)
                        .toString()
                        .lowercase()
                        .contains(search)
                }
            }

        notifyDataSetChanged()
    }

    fun getSelectedApps(): List<String> =
        selected.toList()
}