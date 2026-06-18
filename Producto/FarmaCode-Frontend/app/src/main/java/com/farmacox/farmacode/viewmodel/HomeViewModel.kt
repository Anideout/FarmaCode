package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.dao.entity.ScanHistory
import com.farmacox.farmacode.data.model.Medication
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

    private var allMedications: List<Medication> = emptyList()

    init {
        loadMedications()
        loadScanHistory()
    }

    private fun loadMedications() {
        viewModelScope.launch {
            try {
                repository.getAllMedication().collectLatest { medications ->
                    allMedications = medications
                    val categories = medications
                        .mapNotNull { it.categoriaTerapeutica.takeIf { c -> c.isNotBlank() } }
                        .distinct()
                    _uiState.value = _uiState.value.copy(
                        medications = medications,
                        categories = listOf("Todos") + categories,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun loadScanHistory() {
        viewModelScope.launch {
            try {
                val userId = UserSession.userId ?: 0L
                repository.getRecentScans(userId).collectLatest { history ->
                    _uiState.value = _uiState.value.copy(scanHistory = history)
                }
            } catch (_: Exception) { }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            applyCurrentCategoryFilter()
            return
        }
        if (query.length < 2) return

        viewModelScope.launch {
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
                val filtered = allMedications.filter {
                    it.nombre.contains(query, ignoreCase = true) ||
                    it.principioActivo.contains(query, ignoreCase = true)
                }
                _uiState.value = _uiState.value.copy(medications = filtered)
            }
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyCurrentCategoryFilter(category)
    }

    private fun applyCurrentCategoryFilter(category: String? = _uiState.value.selectedCategory) {
        val filtered = if (category == null || category == "Todos") {
            allMedications
        } else {
            allMedications.filter { it.categoriaTerapeutica == category }
        }
        _uiState.value = _uiState.value.copy(medications = filtered)
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
