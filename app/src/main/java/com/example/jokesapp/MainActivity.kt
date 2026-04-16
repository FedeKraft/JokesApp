package com.example.jokesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.jokesapp.navigation.AppNavigation
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            JokesAppTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = themeViewModel::setDarkTheme
                )
            }
        }
    }
}
