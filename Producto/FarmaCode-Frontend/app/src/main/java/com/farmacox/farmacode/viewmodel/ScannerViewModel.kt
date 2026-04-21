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

data class ScannerUiState(
    val scannedCode: String = "",
    val foundMedication: Medication? = null,
    val alternatives: List<Medication> = emptyList(),
    val isLoading: Boolean = false,
    val showResult: Boolean = false,
    val errorMessage: String? = null
)

class ScannerViewModel(private val repository: MedicationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun simulateScan(code: String) {
        _uiState.value = _uiState.value.copy(
            scannedCode = code,
            isLoading = true,
            errorMessage = null,
            showResult = false
        )

        viewModelScope.launch {
            repository.searchMedications(code).collect { medications ->
                if (medications.isNotEmpty()) {
                    val medication = medications.first()
                    val alternatives = repository.getAlternatives(medication.principioActivo, medication.id)
                    _uiState.value = _uiState.value.copy(
                        foundMedication = medication,
                        alternatives = alternatives,
                        isLoading = false,
                        showResult = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showResult = true,
                        errorMessage = "No se encontró ningún medicamento con el código: $code"
                    )
                }
            }
        }
    }

    fun searchByText(query: String) {
        if (query.isBlank()) return

        _uiState.value = _uiState.value.copy(
            scannedCode = query,
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            repository.searchMedications(query).collect { medications ->
                if (medications.isNotEmpty()) {
                    val medication = medications.first()
                    val alternatives = repository.getAlternatives(medication.principioActivo, medication.id)
                    _uiState.value = _uiState.value.copy(
                        foundMedication = medication,
                        alternatives = alternatives,
                        isLoading = false,
                        showResult = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showResult = true,
                        errorMessage = "No se encontró ningún medicamento para: $query"
                    )
                }
            }
        }
    }

    fun dismissResult() {
        _uiState.value = _uiState.value.copy(
            showResult = false,
            foundMedication = null,
            alternatives = emptyList(),
            scannedCode = ""
        )
    }

    class Factory(private val repository: MedicationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
                return ScannerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
