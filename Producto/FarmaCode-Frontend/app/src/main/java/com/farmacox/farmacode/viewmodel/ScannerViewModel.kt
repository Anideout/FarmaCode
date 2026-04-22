package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.dao.entity.Medication
import com.farmacox.farmacode.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
        val cleanCode = code.trim()
        if (_uiState.value.isLoading || _uiState.value.showResult || cleanCode.isBlank()) return

        _uiState.value = _uiState.value.copy(
            scannedCode = cleanCode,
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                // Usamos split con el caracter literal '|'
                val parts = cleanCode.split('|').map { it.trim() }
                
                if (parts.size >= 8) {
                    // Formato extendido: Nombre|Principio|Dosis|Categoria|Presentacion|Laboratorio|Pais|Descripcion
                    val newMed = Medication(
                        id = "QR-${System.currentTimeMillis()}",
                        nombre = parts[0],
                        principioActivo = parts[1],
                        dosis = parts[2],
                        categoriaTerapeutica = parts[3],
                        presentacion = parts[4],
                        laboratorio = parts[5],
                        paisOrigen = parts[6],
                        descripcion = parts[7],
                        tipo = if (parts.size > 8) parts[8] else "Nuevo",
                        certificacionISP = true
                    )
                    
                    repository.insertMedication(newMed)
                    
                    _uiState.value = _uiState.value.copy(
                        foundMedication = newMed,
                        alternatives = emptyList(),
                        isLoading = false,
                        showResult = true
                    )
                } else if (parts.size >= 4) {
                    // Formato de 4 partes original (mantenemos compatibilidad)
                    val newMed = Medication(
                        id = "QR-${System.currentTimeMillis()}",
                        nombre = parts[0],
                        principioActivo = parts[1],
                        dosis = parts[2],
                        categoriaTerapeutica = parts[3],
                        presentacion = "Caja estándar",
                        laboratorio = "Genérico",
                        paisOrigen = "Chile",
                        descripcion = "Medicamento agregado por QR.",
                        tipo = "Nuevo",
                        certificacionISP = true
                    )
                    repository.insertMedication(newMed)
                    _uiState.value = _uiState.value.copy(foundMedication = newMed, isLoading = false, showResult = true)
                } else {
                    // Búsqueda normal
                    val medications = repository.searchMedications(cleanCode).first()
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
                            errorMessage = "No se encontró el medicamento."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al procesar: ${e.message}"
                )
            }
        }
    }

    fun searchByText(query: String) {
        simulateScan(query)
    }

    fun dismissResult() {
        _uiState.value = _uiState.value.copy(showResult = false, foundMedication = null)
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
