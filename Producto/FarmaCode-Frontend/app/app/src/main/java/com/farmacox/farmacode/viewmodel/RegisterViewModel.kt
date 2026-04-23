package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.dao.entity.User
import com.farmacox.farmacode.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPaswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPaswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun onRegisterClick() {
        val state = _uiState.value

        if (state.name.isBlank() || state.email.isBlank() ||
            state.password.isBlank() || state.confirmPassword.isBlank()
            ) {
            _uiState.value = state.copy(errorMessage = "Debes completar todos los campos...")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.value = state.copy(errorMessage = "email invalido...")
            return
        }

        if (state.password.length < 6) {
            _uiState.value = state.copy(errorMessage = "La contraseña debe contener al menos 6 caracteres")
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            
            val existingUser = userRepository.getUserByEmail(state.email)
            if (existingUser != null) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "El usuario ya existe")
            } else {
                val newUser = User(name = state.name, email = state.email, password = state.password)
                userRepository.insertUser(newUser)
                _uiState.value = _uiState.value.copy(isLoading = false, isRegisterSuccessful = true)
            }
        }
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}