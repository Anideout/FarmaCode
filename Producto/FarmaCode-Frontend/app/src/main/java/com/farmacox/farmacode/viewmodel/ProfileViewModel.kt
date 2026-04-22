package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState (
    val userName: String = "Admin",
    val email: String = "admin@farmacode.com",
    val isNotificacionsEnabled: Boolean = true,
    val isDarkMode: Boolean = false,
    val showSettingsCard: Boolean = false,
    val fontSize: Float = 16f,
    val language: String = "Español"
)

class ProfileViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun toggleNotificacions(enabled: Boolean) {
        _uiState.update { it.copy(isNotificacionsEnabled = enabled)}
    }

    fun toggleDarkMode(enabled:Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled)}
    }

    fun toggleSettingsCard() {
        _uiState.update { it.copy(showSettingsCard = !it.showSettingsCard)}
    }

    fun changeFontSize() {
        //Logica para la font, solo 3 tamaños :3
        _uiState.update {
            val newSize = if (it.fontSize < 24f) it.fontSize + 4f else 14f
            it.copy(fontSize = newSize)
        }
    }

    fun changeLanguage() {
        _uiState.update {
            val newLang = if (it.language == "Español") "English" else "Español"
            it.copy(language = newLang)
        }
    }
}
