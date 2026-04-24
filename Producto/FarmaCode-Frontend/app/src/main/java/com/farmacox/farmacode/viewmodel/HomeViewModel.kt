package com.farmacox.farmacode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmacox.farmacode.data.dao.entity.Medication
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
    val isDarkTheme: Boolean = false
)
class HomeViewModel(private val repository: MedicationRepository): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMedications()
        loadCategories()
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

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            if (query.isBlank()) {
                loadMedications()
            } else {
                repository.searchMedications(query).collectLatest { medications ->
                    _uiState.value = _uiState.value.copy(medications = medications)
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
