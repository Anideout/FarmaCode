package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.dao.entity.ScanHistory
import com.farmacox.farmacode.data.model.Medication
import com.farmacox.farmacode.data.network.RetrofitClient
import com.farmacox.farmacode.data.network.dto.OcrRequest
import com.farmacox.farmacode.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import org.json.JSONObject
import retrofit2.HttpException

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
                val parts = cleanCode.split('|').map { it.trim() }

                if (parts.size >= 8) {
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
                    saveHistory(newMed, "busqueda")
                    _uiState.value = _uiState.value.copy(
                        foundMedication = newMed,
                        alternatives = emptyList(),
                        isLoading = false,
                        showResult = true
                    )
                } else if (parts.size >= 4) {
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
                    saveHistory(newMed, "busqueda")
                    _uiState.value = _uiState.value.copy(foundMedication = newMed, isLoading = false, showResult = true)
                } else {
                    val medications = repository.searchMedications(cleanCode).first()
                    if (medications.isNotEmpty()) {
                        val medication = medications.first()
                        val alternatives = repository.getAlternatives(medication.principioActivo, medication.id)
                        saveHistory(medication, "busqueda")
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

    fun buscarPorTextoOcr(texto: String, imagenBase64: String? = null) {
        if (_uiState.value.isLoading || (texto.isBlank() && imagenBase64 == null)) return

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val response = withTimeout(45000L) {
                    RetrofitClient.busquedaService.buscarPorOcr(OcrRequest(textoOcr = texto, imagenBase64 = imagenBase64 ?: ""))
                }

                if (response.principioActivo == "NO_ES_MEDICAMENTO") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Esto no parece ser un medicamento. Apunta al envase de un medicamento."
                    )
                    return@launch
                }
                if (response.principioActivo == "IMAGEN_ILEGIBLE") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "La imagen no es legible. Intenta con mejor iluminación o enfoca el envase."
                    )
                    return@launch
                }

                if (response.medicamentos.isNotEmpty()) {
                    val medications = response.medicamentos.map { dto ->
                        Medication(
                            id = dto.id.toString(),
                            nombre = dto.nombre.toDisplayCase(),
                            principioActivo = (dto.principioActivo ?: "").toDisplayCase(),
                            dosis = dto.dosis ?: "",
                            presentacion = dto.presentacion ?: "",
                            laboratorio = (dto.laboratorio ?: "").toDisplayCase(),
                            paisOrigen = dto.paisOrigen ?: "",
                            tipo = dto.tipo ?: "",
                            categoriaTerapeutica = dto.categoriaTerapeutica ?: "",
                            certificacionISP = dto.certificacionISP ?: false,
                            descripcion = dto.descripcion ?: ""
                        )
                    }
                    saveHistory(medications.first(), "ocr")
                    _uiState.value = _uiState.value.copy(
                        foundMedication = medications.first(),
                        alternatives = medications.drop(1),
                        isLoading = false,
                        showResult = true,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No se encontró el medicamento en la base de datos."
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Tiempo de espera agotado. Verificá la conexión al backend."
                )
            } catch (e: HttpException) {
                val errorMsg = try {
                    val body = e.response()?.errorBody()?.string() ?: ""
                    JSONObject(body).optString("message", e.message ?: "HTTP ${e.code()}")
                } catch (_: Exception) {
                    e.message ?: "HTTP ${e.code()}"
                }
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    private suspend fun saveHistory(medication: Medication, origen: String) {
        repository.saveScanHistory(
            ScanHistory(
                userId = UserSession.userId ?: 0L,
                medicationId = medication.id,
                nombre = medication.nombre,
                principioActivo = medication.principioActivo,
                dosis = medication.dosis,
                presentacion = medication.presentacion,
                laboratorio = medication.laboratorio,
                paisOrigen = medication.paisOrigen,
                tipo = medication.tipo,
                categoriaTerapeutica = medication.categoriaTerapeutica,
                certificacionISP = medication.certificacionISP,
                descripcion = medication.descripcion,
                origen = origen
            )
        )
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
    }

    fun selectAlternative(medication: Medication) {
        val current = _uiState.value
        val remaining = current.alternatives.filter { it.id != medication.id }
        val previous = current.foundMedication
        _uiState.value = current.copy(
            foundMedication = medication,
            alternatives = if (previous != null) listOf(previous) + remaining else remaining
        )
    }

    fun dismissResult() {
        _uiState.value = _uiState.value.copy(showResult = false, foundMedication = null)
    }

    private fun String.toDisplayCase(): String {
        val letters = filter { it.isLetter() }
        if (letters.isEmpty()) return this
        val upperCount = letters.count { it.isUpperCase() }
        if (upperCount.toDouble() / letters.length < 0.75) return this
        return split(" ").joinToString(" ") { w ->
            w.lowercase().replaceFirstChar { it.uppercaseChar() }
        }
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
