package com.farmacox.farmacode.repository

import com.farmacox.farmacode.data.dao.MedicationDao
import com.farmacox.farmacode.data.dao.entity.Medication
import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val medicationDao: MedicationDao) {

    fun getAllMedication(): Flow<List<Medication>> = medicationDao.getAllMedications()

    suspend fun getMedicationById(id: String): Medication? = medicationDao.getMedicationById(id)

    fun searchMedications(query: String): Flow<List<Medication>> = medicationDao.searchMedications(query)

    fun getMedicationsByCategory(category: String): Flow<List<Medication>> = medicationDao.getMedicationsByCategory(category)

    suspend fun getAlternatives(activeIngredient: String, currentId: String): List<Medication> = medicationDao.getAlternatives(activeIngredient, currentId)

    fun getAllCategories(): Flow<List<String>> = medicationDao.getAllCategories()
}