package com.focusbuddy.helper

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
class MainActivity : FragmentActivity() {

    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only authenticate when MainActivity is actually CREATED.
        authenticateForApp()
    }

    private fun authenticateForApp() {

        showBiometricPrompt(
            title = "Unlock FocusBuddy",
            subtitle = "Verify your device credentials to continue",

            onSuccess = {
                showMainScreen()
            },

            onError = {
                Toast.makeText(
                    this,
                    "FocusBuddy is locked.",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        )
    }


    private fun showBiometricPrompt(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {

        val biometricManager =
            BiometricManager.from(this)

        if (
            biometricManager.canAuthenticate(authenticators)
            != BiometricManager.BIOMETRIC_SUCCESS
        ) {

            Toast.makeText(
                this,
                "Please set up a phone screen lock to continue.",
                Toast.LENGTH_LONG
            ).show()

            onError()
            return
        }

        val executor =
            ContextCompat.getMainExecutor(this)

        val biometricPrompt =
            BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)

                        onSuccess()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()

                        Toast.makeText(
                            this@MainActivity,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(
                            errorCode,
                            errString
                        )

                        onError()
                    }
                }
            )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setAllowedAuthenticators(authenticators)
                .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showMainScreen() {

        setContent {

            MainScreen(

                onResetClick = {
                    authenticateForReset()
                },

                onChooseAppsClick = {

                    startActivity(
                        Intent(
                            this,
                            AppListActivity::class.java
                        )
                    )
                }
            )
        }
    }

    private fun authenticateForReset() {

        showBiometricPrompt(

            title = "End Focus Session?",

            subtitle =
                "Verify your device credentials to change FocusBuddy.",

            onSuccess = {
                resetSavedApps()
            },

            onError = {
                Toast.makeText(
                    this,
                    "Focus session was not changed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun resetSavedApps() {

        val prefs =
            getSharedPreferences(
                "focus_prefs",
                MODE_PRIVATE
            )

        val savedApps =
            prefs.getStringSet(
                "blocked_apps",
                emptySet()
            )

        if (savedApps.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "No apps are currently selected.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        prefs.edit()
            .remove("blocked_apps")
            .remove("focus_end_time")
            .apply()

        stopService(
            Intent(
                this,
                FocusMonitorService::class.java
            )
        )

        Toast.makeText(
            this,
            "Blocked apps have been reset.",
            Toast.LENGTH_SHORT
        ).show()
    }

    /*
     * IMPORTANT:
     *
     * When another Activity uses FLAG_ACTIVITY_SINGLE_TOP,
     * Android sends the Intent here instead of creating
     * another MainActivity.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}


/* ------------------------------------------------ */
/*                    MAIN SCREEN                   */
/* ------------------------------------------------ */

@Composable
fun MainScreen(
    onResetClick: () -> Unit,
    onChooseAppsClick: () -> Unit
) {
    val context = LocalContext.current

    var remainingTime by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(Unit) {

        while (true) {

            val prefs =
                context.getSharedPreferences(
                    "focus_prefs",
                    Context.MODE_PRIVATE
                )

            val endTime =
                prefs.getLong(
                    "focus_end_time",
                    0L
                )

            val remaining =
                endTime - System.currentTimeMillis()

            remainingTime =
                if (remaining > 0) {
                    remaining
                } else {
                    0L
                }

            delay(1000)
        }
    }
    val totalSeconds =
        remainingTime / 1000

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60
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
                text = "",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            if (remainingTime > 0) {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Focus session active",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = String.format(
                        "%02d:%02d:%02d",
                        hours,
                        minutes,
                        seconds
                    ),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "remaining",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }

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