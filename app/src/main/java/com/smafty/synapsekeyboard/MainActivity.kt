package com.smafty.synapsekeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smafty.synapsekeyboard.auth.AuthManager
import com.smafty.synapsekeyboard.ui.screens.KeyboardTestScreen
import com.smafty.synapsekeyboard.ui.screens.LoginScreen
import com.smafty.synapsekeyboard.ui.screens.MainDashboard
import com.smafty.synapsekeyboard.ui.screens.SplashScreen
import com.smafty.synapsekeyboard.ui.theme.SynapseKeyboardTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize dynamic app theme from local Room database
        com.smafty.synapsekeyboard.ui.theme.ThemeManager.initialize(applicationContext)

        // ── Global Uncaught Exception Handler ──
        // Capture any unexpected crashes, write them to filesDir/last_crash.txt,
        // and allow the user to copy the stack trace on the next launch.
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val file = java.io.File(filesDir, "last_crash.txt")
                val writer = java.io.PrintWriter(java.io.FileWriter(file))
                throwable.printStackTrace(writer)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                // Ignore
            }
            oldHandler?.uncaughtException(thread, throwable)
        }

        setContent {
            SynapseKeyboardTheme {
                // Read previous crash if any
                var previousCrash by remember { mutableStateOf<String?>(null) }
                remember {
                    val file = java.io.File(filesDir, "last_crash.txt")
                    if (file.exists()) {
                        try {
                            previousCrash = file.readText()
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    true
                }

                // Beautiful, Material3 diagnostic crash report dialog
                if (previousCrash != null) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = {
                            try {
                                java.io.File(filesDir, "last_crash.txt").delete()
                            } catch (e: Exception) {}
                            previousCrash = null
                        },
                        title = {
                            androidx.compose.material3.Text(
                                text = "Previous App Crash Detected",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        text = {
                            androidx.compose.foundation.lazy.LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                item {
                                    androidx.compose.material3.Text(
                                        text = previousCrash ?: "",
                                        style = androidx.compose.ui.text.TextStyle(
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            fontSize = 11.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    try {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Crash Log", previousCrash)
                                        clipboard.setPrimaryClip(clip)
                                        android.widget.Toast.makeText(context, "Copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {}
                                }
                            ) {
                                androidx.compose.material3.Text("Copy Details")
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    try {
                                        java.io.File(filesDir, "last_crash.txt").delete()
                                    } catch (e: Exception) {}
                                    previousCrash = null
                                }
                            ) {
                                androidx.compose.material3.Text("Dismiss")
                            }
                        }
                    )
                }

                // ── Auth-aware start destination ──────────────────────────────
                // Determined asynchronously; show splash while we check the session.
                // "splash" is the safe initial value — it checks auth and routes onwards.
                var startDestination by remember { mutableStateOf("splash") }
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController    = navController,
                        startDestination = startDestination
                    ) {
                        // ── Splash ────────────────────────────────────────────
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    // After the splash animation, decide where to go:
                                    //   • Logged in  → dashboard (skip login + onboarding)
                                    //   • Not logged → login screen
                                    lifecycleScope.launch {
                                        val destination =
                                            if (AuthManager.isLoggedIn()) "main" else "login"
                                        navController.navigate(destination) {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        // ── Login ─────────────────────────────────────────────
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ── Main Dashboard ────────────────────────────────────
                        composable("main") {
                            MainDashboard(
                                onNavigateToKeyboardTest = {
                                    navController.navigate("keyboardTest")
                                },
                                onSignOut = {
                                    navController.navigate("login") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ── Keyboard Test ─────────────────────────────────────
                        composable("keyboardTest") {
                            KeyboardTestScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
