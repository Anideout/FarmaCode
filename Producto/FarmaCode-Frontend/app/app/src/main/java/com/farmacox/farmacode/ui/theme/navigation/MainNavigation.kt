package com.farmacox.farmacode.ui.theme.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.farmacode.app.ui.screens.chat.ChatScreen
import com.farmacox.farmacode.ui.theme.screens.ProfileScreen
import com.farmacox.farmacode.ui.theme.screens.HelpScreen
import com.farmacox.farmacode.ui.theme.screens.HomeScreen
import com.farmacox.farmacode.ui.theme.screens.LoginScreen
import com.farmacox.farmacode.ui.theme.screens.RegisterScreen
import com.farmacox.farmacode.ui.theme.screens.ScannerScreen
import com.farmacox.farmacode.ui.theme.theme.PrimaryGreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

// Pantallas que NO muestran la barra de navegación inferior
private val screensWithoutBottomBar = listOf(Screen.Login.route, Screen.Register.route)

@Composable
fun MainNavigation(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    fontSize: Float,
    onFontSizeChange: () -> Unit,
    language: String,
    onLanguageChange: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem(Screen.Home.route, if(language == "Español") "Inicio" else "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Screen.Scanner.route, "Scanner", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner),
        BottomNavItem(Screen.Chat.route, "Chat", Icons.Filled.Chat, Icons.Outlined.Chat),
        BottomNavItem(Screen.Help.route, if(language == "Español") "Ayuda" else "Help", Icons.Filled.Help, Icons.Outlined.Help),
        BottomNavItem(Screen.Profile.route, if(language == "Español") "Perfil" else "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in screensWithoutBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, fontSize = (fontSize - 4).sp) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryGreen,
                                selectedTextColor = PrimaryGreen,
                                indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController = navController)
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    isDarkTheme = isDarkTheme, 
                    onToggleTheme = onToggleTheme,
                    fontSize = fontSize,
                    language = language
                )
            }
            composable(Screen.Scanner.route) {
                ScannerScreen(
                    fontSize = fontSize,
                    language = language
                )
            }
            composable(Screen.Chat.route) {
                ChatScreen(
                    fontSize = fontSize,
                    language = language
                )
            }
            composable(Screen.Help.route) {
                HelpScreen(
                    fontSize = fontSize,
                    language = language
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    fontSize = fontSize,
                    onFontSizeChange = onFontSizeChange,
                    language = language,
                    onLanguageChange = onLanguageChange
                )
            }
        }
    }
}