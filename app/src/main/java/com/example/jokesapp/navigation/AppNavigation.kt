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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jokesapp.ui.categories.CategoriesScreen
import com.example.jokesapp.ui.favorites.FavoritesScreen
import com.example.jokesapp.ui.home.HomeScreen
import com.example.jokesapp.ui.profile.ProfileScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Categories : Screen("categories", "Categories", Icons.Filled.List)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
}

private val bottomNavItems = listOf(
    Screen.Home,
    Screen.Categories,
    Screen.Favorites,
    Screen.Profile
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
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
            composable(Screen.Home.route) { HomeScreen(innerPadding) }
            composable(Screen.Categories.route) { CategoriesScreen(innerPadding) }
            composable(Screen.Favorites.route) { FavoritesScreen(innerPadding) }
            composable(Screen.Profile.route) { ProfileScreen(innerPadding) }
        }
    }
}
