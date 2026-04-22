package com.farmacox.farmacode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.farmacox.farmacode.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    fontSize: Float,
    onFontSizeChange: () -> Unit,
    language: String,
    onLanguageChange: () -> Unit,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEnglish = language == "English"

    // Traducciones
    val texts = if (isEnglish) {
        mapOf(
            "history" to "History",
            "notifications" to "Notifications",
            "darkMode" to "Dark Mode",
            "help" to "Help",
            "settings" to "Settings",
            "logout" to "Log Out",
            "configTitle" to "Global Settings",
            "changeFont" to "Change App Font Size",
            "language" to "App Language",
            "close" to "Save & Close",
            "userLabel" to "Administrator"
        )
    } else {
        mapOf(
            "history" to "Historial",
            "notifications" to "Notificaciones",
            "darkMode" to "Tema Oscuro",
            "help" to "Ayuda",
            "settings" to "Configuración",
            "logout" to "Cerrar Sesión",
            "configTitle" to "Ajustes Globales",
            "changeFont" to "Cambiar Tamaño de Fuente",
            "language" to "Idioma de la App",
            "close" to "Guardar y Cerrar",
            "userLabel" to "Administrador"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // Avatar con estilo
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(60.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(10.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = texts["userLabel"]!!, 
            fontSize = (fontSize + 6).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = uiState.email, 
            fontSize = fontSize.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botones de acción principal
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Historial
                TextButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(texts["history"]!!, fontSize = fontSize.sp)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), thickness = 0.5.dp)

                // Ayuda
                TextButton(
                    onClick = { navController.navigate("help") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Help, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(texts["help"]!!, fontSize = fontSize.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggles Rápidos
        Text(
            text = "Preferencia de vista",
            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 8.dp),
            fontSize = (fontSize - 2).sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        // Notificaciones
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(texts["notifications"]!!, fontSize = fontSize.sp)
            Switch(
                checked = uiState.isNotificacionsEnabled,
                onCheckedChange = { viewModel.toggleNotificacions(it) }
            )
        }

        // Tema Oscuro
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(texts["darkMode"]!!, fontSize = fontSize.sp)
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onToggleTheme() }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón Configuración (Abre el diálogo mejorado)
        Button(
            onClick = { viewModel.toggleSettingsCard() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(texts["settings"]!!, fontSize = fontSize.sp, fontWeight = FontWeight.SemiBold)
        }

        // Cerrar Sesión
        TextButton(
            onClick = { navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }},
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text(texts["logout"]!!, fontSize = fontSize.sp)
        }

        // --- DIÁLOGO DE CONFIGURACIÓN GLOBAL MEJORADO ---
        if (uiState.showSettingsCard) {
            Dialog(onDismissRequest = { viewModel.toggleSettingsCard() }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = texts["configTitle"]!!,
                            fontSize = (fontSize + 6).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Opción de Tamaño de Fuente
                        Text(
                            text = texts["changeFont"]!!,
                            fontSize = (fontSize - 1).sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onFontSizeChange() },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Icon(Icons.Default.TextFields, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Aa (${fontSize.toInt()})",
                                fontSize = fontSize.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Opción de Idioma
                        Text(
                            text = texts["language"]!!,
                            fontSize = (fontSize - 1).sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onLanguageChange() },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                language,
                                fontSize = fontSize.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = { viewModel.toggleSettingsCard() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text(texts["close"]!!, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
