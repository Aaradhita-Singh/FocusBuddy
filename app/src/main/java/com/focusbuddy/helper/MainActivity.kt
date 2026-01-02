package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                onResetClick = { resetSavedApps() },
                onChooseAppsClick = {
                    startActivity(Intent(this, AppListActivity::class.java))
                }
            )
        }
    }

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
}

@Composable
fun MainScreen(
    onResetClick: () -> Unit,
    onChooseAppsClick: () -> Unit
) {
    // Gradient colors similar to BlockedActivity
    val gradientStart = Color(0xFFFFA27C)
    val gradientEnd = Color(0xFF2575FC)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientStart, gradientEnd)
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "FocusBuddy",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "The tool to block distractions",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Choose Apps Button
            Button(
                onClick = onChooseAppsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = gradientEnd
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Choose Apps",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Button
            Button(
                onClick = onResetClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.3f),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Reset Selections",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
