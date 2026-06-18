package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onLoginClick() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Completa todos los campos")
            return
        }

        if (!state.email.matches(EMAIL_REGEX)) {
            _uiState.value = state.copy(errorMessage = "Email inválido")
            return
        }

        _uiState.value = state.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val response = userRepository.loginOnBackend(state.email, state.password)
                UserSession.userId = response.id
                UserSession.userEmail = response.email
                userRepository.insertUser(
                    com.farmacox.farmacode.data.dao.entity.User(
                        name = response.nombre, email = response.email, password = ""
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false, isLoginSuccessful = true)
            } catch (e: retrofit2.HttpException) {
                val msg = if (e.code() == 400) "Credenciales incorrectas" else "Error del servidor"
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = msg)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Sin conexión al servidor")
            }
        }
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
