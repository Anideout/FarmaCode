package com.farmacox.farmacode.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Scanner : Screen("scanner")
    object Chat : Screen("chat")
    object Help : Screen("help")
    object Profile : Screen("profile")
}