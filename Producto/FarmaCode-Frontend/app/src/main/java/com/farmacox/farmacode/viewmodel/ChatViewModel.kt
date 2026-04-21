package com.farmacox.farmacode.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.dao.entity.Medication
import com.farmacox.farmacode.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val scannedMedication: Medication? = null
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val isUser: Boolean,
    val medication: Medication? = null
)

class ChatViewModel(private val repository: MedicationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ChatUiState(
            messages = listOf(
                ChatMessage(
                    content = "¡Hola! Soy tu asistente de FarmaCode. ¿En qué puedo ayudarte hoy? Puedo buscar medicamentos, explicarte sobre principios activos o encontrar alternativas.",
                    isUser = false
                )
            )
        )
    }

    fun sendMessage(message: String) {
        val userMessage = ChatMessage(content = message, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true
        )

        viewModelScope.launch {
            val response = processUserMessage(message)
            val assistantMessage = ChatMessage(content = response, isUser = false)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + assistantMessage,
                isLoading = false
            )
        }
    }

    private suspend fun processUserMessage(message: String): String {
        val query = message.lowercase()
        val keywords = listOf("buscar", "busca", "encuentra", "dime", "muéstrame", "cual", "que es")

        return when {
            query.contains("alternativa") || query.contains("genérico") -> {
                "Para buscar alternativas a un medicamento específico, puedes ir a la pantalla de inicio y seleccionar el medicamento que estás buscando. Allí verás todas las alternativas disponibles con el mismo principio activo."
            }
            query.contains("hola") || query.contains("buenos") || query.contains("saludos") -> {
                "¡Hola! 👋 ¿Cómo estás? Puedo ayudarte a encontrar información sobre medicamentos certificados por el ISP. ¿Qué necesitas?"
            }
            query.contains("isp") || query.contains("certificación") -> {
                "Todos los medicamentos en nuestra base de datos están certificados por el Instituto de Salud Pública de Chile (ISP). Esta certificación garantiza que el medicamento cumple con los estándares de calidad, seguridad y eficacia establecidos."
            }
            keywords.any { query.contains(it) } -> {
                val searchTerm = query.replace(keywords.first { query.contains(it) }, "").trim()
                if (searchTerm.isNotEmpty()) {
                    val medications = repository.searchMedications(searchTerm)
                    medications.collect { meds ->
                        if (meds.isNotEmpty()) {
                            _uiState.value = _uiState.value.copy(scannedMedication = meds.first())
                        }
                    }
                    "Encontré algunos medicamentos relacionados con '$searchTerm'. En la pantalla de inicio puedes ver todos los resultados de tu búsqueda."
                } else {
                    "Indícame el nombre del medicamento o principio activo que buscas."
                }
            }
            else -> {
                "Lo siento, no entendí tu consulta. Puedo ayudarte buscando medicamentos por nombre, principio activo o laboratorio. También puedo mostrarte alternativas genéricas y bioequivalentes."
            }
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
