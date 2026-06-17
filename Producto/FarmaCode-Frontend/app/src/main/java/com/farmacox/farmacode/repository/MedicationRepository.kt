package com.farmacox.farmacode.repository

import com.farmacox.farmacode.data.model.Medication
import com.farmacox.farmacode.data.network.FarmaCodeApiService
import com.farmacox.farmacode.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedicationRepository(
    private val apiService: FarmaCodeApiService = RetrofitClient.apiService
) {

    fun getAllMedication(): Flow<List<Medication>> = flow {
        try {
            val response = apiService.getAllMedicamentos(size = 1000)
            val medications = response.content.map { it.toMedication() }
            emit(medications)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getMedicationById(id: String): Medication? {
        return try {
            val response = apiService.getMedicamentoById(id.toLong())
            response.toMedication()
        } catch (e: Exception) {
            null
        }
    }

    fun searchMedications(query: String): Flow<List<Medication>> = flow {
        try {
            val response = apiService.searchMedicamentos(query)
            val medications = response.map { it.toMedication() }
            emit(medications)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getMedicationsByCategory(category: String): Flow<List<Medication>> = flow {
        // API doesn't have category endpoint, filter from all meds
        try {
            val response = apiService.getAllMedicamentos(size = 1000)
            val medications = response.content
                .filter { it.categoriaTerapeutica == category }
                .map { it.toMedication() }
            emit(medications)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getAlternatives(activeIngredient: String, currentId: String): List<Medication> {
        return try {
            val response = apiService.getMedicamentosByPrincipioActivo(activeIngredient)
            response
                .map { it.toMedication() }
                .filter { it.id != currentId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAllCategories(): Flow<List<String>> = flow {
        try {
            val response = apiService.getAllMedicamentos(size = 1000)
            val categories = response.content
                .mapNotNull { it.categoriaTerapeutica }
                .distinct()
            emit(categories)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
