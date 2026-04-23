package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Singleton temporal para la sesión (en una app real usarías DataStore o Room)
object UserSession {
    var userEmail: String? = null
}

data class ProfileUiState (
    val userName: String = "",
    val email: String = "",
    val isNotificacionsEnabled: Boolean = true,
    val isDarkMode: Boolean = false,
    val showSettingsCard: Boolean = false,
    val fontSize: Float = 16f,
    val language: String = "Español"
)

class ProfileViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val email = UserSession.userEmail
        if (email != null) {
            viewModelScope.launch {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    _uiState.update { it.copy(userName = user.name, email = user.email) }
                }
            }
        }
    }

    fun toggleNotificacions(enabled: Boolean) {
        _uiState.update { it.copy(isNotificacionsEnabled = enabled)}
    }

    fun toggleSettingsCard() {
        _uiState.update { it.copy(showSettingsCard = !it.showSettingsCard)}
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(repository) as T
        }
    }
}
