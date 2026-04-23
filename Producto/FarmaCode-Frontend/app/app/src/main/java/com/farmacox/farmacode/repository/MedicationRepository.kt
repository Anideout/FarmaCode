package com.farmacox.farmacode.repository

import com.farmacox.farmacode.data.dao.entity.Medication
import com.farmacox.farmacode.data.network.FarmaCodeApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedicationRepository(private val apiService: FarmaCodeApiService) {

    fun getAllMedication(): Flow<List<Medication>> = flow {
        val response = apiService.getAllMedicamentos(size = 100)
        emit(response.content.map { it.toMedication() })
    }

    suspend fun getMedicationById(id: String): Medication? {
        return try {
            apiService.getMedicamentoById(id.toLong()).toMedication()
        } catch (e: Exception) {
            null
        }
    }

    fun searchMedications(query: String): Flow<List<Medication>> = flow {
        if (query.isBlank()) {
            val response = apiService.getAllMedicamentos(size = 100)
            emit(response.content.map { it.toMedication() })
        } else {
            val results = apiService.searchMedicamentos(query)
            emit(results.map { it.toMedication() })
        }
    }

    fun getMedicationsByCategory(category: String): Flow<List<Medication>> = flow {
        val response = apiService.getAllMedicamentos(size = 100)
        val filtered = response.content
            .filter { it.categoriaTerapeutica == category }
            .map { it.toMedication() }
        emit(filtered)
    }

    suspend fun getAlternatives(activeIngredient: String, currentId: String): List<Medication> {
        return try {
            apiService.getMedicamentosByPrincipioActivo(activeIngredient)
                .filter { it.id.toString() != currentId }
                .map { it.toMedication() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAllCategories(): Flow<List<String>> = flow {
        val response = apiService.getAllMedicamentos(size = 100)
        val categories = response.content
            .mapNotNull { it.categoriaTerapeutica }
            .distinct()
            .sorted()
        emit(categories)
    }

    suspend fun insertMedication(medication: Medication) {
        // No-op: el catálogo es administrado por el servidor
    }

    suspend fun deleteMedication(medication: Medication) {
        // No-op: el catálogo es administrado por el servidor
    }
}
