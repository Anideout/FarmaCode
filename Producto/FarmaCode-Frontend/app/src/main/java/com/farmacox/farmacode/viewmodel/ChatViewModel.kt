package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.network.RetrofitClient
import com.farmacox.farmacode.data.network.dto.ChatRequest
import com.farmacox.farmacode.data.network.dto.ChatTurn
import com.farmacox.farmacode.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val isUser: Boolean
)

class ChatViewModel(private val repository: MedicationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Historial que se envía a la API (solo los turnos confirmados)
    private val apiHistory = mutableListOf<ChatTurn>()

    init {
        _uiState.value = ChatUiState(
            messages = listOf(
                ChatMessage(
                    content = "¡Hola! Soy tu asistente farmacéutico de FarmaCode. " +
                            "Puedo ayudarte con dudas sobre medicamentos, principios activos, " +
                            "dosis, efectos secundarios y alternativas genéricas. ¿En qué te puedo ayudar?",
                    isUser = false
                )
            )
        )
    }

    fun sendMessage(message: String) {
        if (_uiState.value.isLoading) return
        val userMsg = ChatMessage(content = message, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMsg,
            isLoading = true
        )

        viewModelScope.launch {
            var callSucceeded = false
            val respuesta = try {
                val response = RetrofitClient.busquedaService.chat(
                    ChatRequest(mensaje = message, historial = apiHistory.toList())
                )
                callSucceeded = true
                response.respuesta
            } catch (e: Exception) {
                "Lo siento, no pude conectarme al servidor. Por favor intenta nuevamente."
            }

            if (callSucceeded) {
                apiHistory.add(ChatTurn(rol = "user", contenido = message))
                apiHistory.add(ChatTurn(rol = "model", contenido = respuesta))
            }

            val assistantMsg = ChatMessage(content = respuesta, isUser = false)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + assistantMsg,
                isLoading = false
            )
        }
    }

    class Factory(private val repository: MedicationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
