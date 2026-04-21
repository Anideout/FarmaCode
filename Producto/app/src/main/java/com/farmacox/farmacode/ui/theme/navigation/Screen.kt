package com.farmacox.farmacode.ui.theme.navigation

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Scanner: Screen("scanner")
    object Chat: Screen("chat")
    object Help: Screen("help")
    object Profile: Screen("profile")
}