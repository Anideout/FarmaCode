package com.farmacox.farmacode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.farmacox.farmacode.ui.theme.navigation.MainNavigation
import com.farmacox.farmacode.ui.theme.theme.FarmaCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var fontSize by remember { mutableFloatStateOf(16f) }
            var language by remember { mutableStateOf("Español") }

            FarmaCodeTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme },
                        fontSize = fontSize,
                        onFontSizeChange = {
                            fontSize = if (fontSize < 24f) fontSize + 4f else 14f
                        },
                        language = language,
                        onLanguageChange = {
                            language = if (language == "Español") "English" else "Español"
                        }
                    )
                }
            }
        }
    }
}
