package com.example.jokesapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jokesapp.R
import com.example.jokesapp.ui.categories.CategoriesScreen
import com.example.jokesapp.ui.favorites.FavoritesScreen
import com.example.jokesapp.ui.home.HomeScreen
import com.example.jokesapp.ui.profile.ProfileScreen
import com.example.jokesapp.viewmodel.JokeViewModel

sealed class Screen(val route: String, val labelRes: Int, val icon: ImageVector) {
    object Home       : Screen("home",       R.string.nav_home,       Icons.Filled.Home)
    object Categories : Screen("categories", R.string.nav_categories, Icons.Filled.List)
    object Favorites  : Screen("favorites",  R.string.nav_favorites,  Icons.Filled.Favorite)
    object Profile    : Screen("profile",    R.string.nav_profile,    Icons.Filled.Person)
}

private val bottomNavItems = listOf(
    Screen.Home,
    Screen.Categories,
    Screen.Favorites,
    Screen.Profile
)

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val jokeViewModel: JokeViewModel = viewModel()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelRes)) },
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(innerPadding, jokeViewModel)
            }
            composable(Screen.Categories.route) {
                CategoriesScreen(innerPadding)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    outerPadding = innerPadding,
                    viewModel = jokeViewModel,
                    onGoFindJokes = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    outerPadding = innerPadding,
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = onDarkThemeChange
                )
            }
        }
    }
}
