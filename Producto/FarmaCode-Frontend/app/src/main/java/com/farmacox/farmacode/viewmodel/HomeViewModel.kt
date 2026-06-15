package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.dao.entity.Medication
import com.farmacox.farmacode.data.dao.entity.ScanHistory
import com.farmacox.farmacode.data.network.RetrofitClient
import com.farmacox.farmacode.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class HomeUiState(
    val medications: List<Medication> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val selectedMedication: Medication? = null,
    val alternatives: List<Medication> = emptyList(),
    val isLoading: Boolean = true,
    val isDarkTheme: Boolean = false,
    val scanHistory: List<ScanHistory> = emptyList(),
    val historyFilter: String = "Todos"
)

class HomeViewModel(private val repository: MedicationRepository): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMedications()
        loadCategories()
        loadScanHistory()
    }

    private fun loadMedications() {
        viewModelScope.launch {
            repository.getAllMedication().collectLatest { medications ->
                _uiState.value = _uiState.value.copy(
                    medications = medications,
                    isLoading = false
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collectLatest { categories ->
                _uiState.value = _uiState.value.copy(categories = listOf("Todos") + categories)
            }
        }
    }

    private fun loadScanHistory() {
        viewModelScope.launch {
            repository.getRecentScans().collectLatest { history ->
                _uiState.value = _uiState.value.copy(scanHistory = history)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            if (query.isBlank()) {
                loadMedications()
            } else if (query.length >= 2) {
                try {
                    val dtos = RetrofitClient.busquedaService.buscarMedicamentos(query)
                    val medications = dtos.map { dto ->
                        Medication(
                            id = dto.id.toString(),
                            nombre = dto.nombre,
                            principioActivo = dto.principioActivo ?: "",
                            dosis = dto.dosis ?: "",
                            presentacion = dto.presentacion ?: "",
                            laboratorio = dto.laboratorio ?: "",
                            paisOrigen = dto.paisOrigen ?: "",
                            tipo = dto.tipo ?: "",
                            categoriaTerapeutica = dto.categoriaTerapeutica ?: "",
                            certificacionISP = dto.certificacionISP ?: false,
                            descripcion = dto.descripcion ?: ""
                        )
                    }
                    _uiState.value = _uiState.value.copy(medications = medications)
                } catch (e: Exception) {
                    repository.searchMedications(query).collectLatest { medications ->
                        _uiState.value = _uiState.value.copy(medications = medications)
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        viewModelScope.launch {
            if (category == null || category == "Todos") {
                loadMedications()
            } else {
                repository.getMedicationsByCategory(category).collectLatest { medications ->
                    _uiState.value = _uiState.value.copy(medications = medications)
                }
            }
        }
    }

    fun onMedicationSelected(medication: Medication) {
        viewModelScope.launch {
            val alternatives = repository.getAlternatives(medication.principioActivo, medication.id)
            _uiState.value = _uiState.value.copy(
                selectedMedication = medication,
                alternatives = alternatives
            )
        }
    }

    fun onScanHistorySelected(scan: ScanHistory) {
        val medication = Medication(
            id = scan.medicationId,
            nombre = scan.nombre,
            principioActivo = scan.principioActivo,
            dosis = scan.dosis,
            presentacion = scan.presentacion,
            laboratorio = scan.laboratorio,
            paisOrigen = scan.paisOrigen,
            tipo = scan.tipo,
            categoriaTerapeutica = scan.categoriaTerapeutica,
            certificacionISP = scan.certificacionISP,
            descripcion = scan.descripcion
        )
        onMedicationSelected(medication)
    }

    fun onDeleteScanHistory(scan: ScanHistory) {
        viewModelScope.launch {
            repository.deleteScanHistory(scan)
        }
    }

    fun onHistoryFilterChange(filter: String) {
        _uiState.value = _uiState.value.copy(historyFilter = filter)
    }

    fun onDismissDialog() {
        _uiState.value = _uiState.value.copy(selectedMedication = null, alternatives = emptyList())
    }

    fun toggleTheme() {
        _uiState.value = uiState.value.copy(isDarkTheme = !_uiState.value.isDarkTheme)
    }

    class Factory(private val repository: MedicationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}
