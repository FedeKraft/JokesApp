package com.example.jokesapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.jokesapp.navigation.AppNavigation
import com.example.jokesapp.notifications.NotificationHelper
import com.example.jokesapp.notifications.NotificationOnboardingScreen
import com.example.jokesapp.ui.auth.AuthScreen
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.viewmodel.ThemeViewModel

class MainActivity : AppCompatActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            JokesAppTheme(darkTheme = isDarkTheme) {
                var notificationOnboardingDone by rememberSaveable {
                    mutableStateOf(isNotificationOnboardingCompleted())
                }
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }

                when {
                    !notificationOnboardingDone && shouldRequestNotificationPermission() -> {
                        NotificationOnboardingScreen {
                            markNotificationOnboardingCompleted()
                            notificationOnboardingDone = true
                        }
                    }
                    !isAuthenticated -> {
                        AuthScreen { isAuthenticated = true }
                    }
                    else -> {
                        AppNavigation(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeChange = themeViewModel::setDarkTheme,
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        NotificationHelper.scheduleAppClosedNotification(this)
    }

    override fun onStart() {
        super.onStart()
        NotificationHelper.cancelAppClosedNotification(this)
    }

    private fun shouldRequestNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        return checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
    }

    private fun isNotificationOnboardingCompleted(): Boolean =
        getPreferences(Context.MODE_PRIVATE)
            .getBoolean("notification_onboarding_done", false)

    private fun markNotificationOnboardingCompleted() {
        getPreferences(Context.MODE_PRIVATE)
            .edit().putBoolean("notification_onboarding_done", true).apply()
    }
}
