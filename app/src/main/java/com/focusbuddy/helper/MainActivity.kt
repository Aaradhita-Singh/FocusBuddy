package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EntryScreen(
                onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                },
                onSignupClick = {
                    startActivity(Intent(this, SignupActivity::class.java))
                },
                onGuestClick = {
                    startActivity(Intent(this, AppListActivity::class.java))
                }
            )
        }
    }
}
@Composable
fun EntryScreen(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onGuestClick: () -> Unit
) {

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFA27C), Color(0xFF2575FC))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "FOCUSBUDDY",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(55.dp)
            ) {
                Text("LOG IN", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignupClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(55.dp)
            ) {
                Text("CREATE ACCOUNT", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onGuestClick) {
                Text(
                    "Continue as Guest",
                    color = Color.White
                )
            }
        }
    }
}
