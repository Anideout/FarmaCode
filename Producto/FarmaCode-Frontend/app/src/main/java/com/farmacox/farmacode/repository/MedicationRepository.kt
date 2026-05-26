package com.farmacox.farmacode.repository

import com.farmacox.farmacode.data.dao.MedicationDao
import com.farmacox.farmacode.data.dao.ScanHistoryDao
import com.farmacox.farmacode.data.dao.entity.Medication
import com.farmacox.farmacode.data.dao.entity.ScanHistory
import kotlinx.coroutines.flow.Flow

class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val scanHistoryDao: ScanHistoryDao
) {

    fun getAllMedication(): Flow<List<Medication>> = medicationDao.getAllMedications()

    suspend fun getMedicationById(id: String): Medication? = medicationDao.getMedicationById(id)

    fun searchMedications(query: String): Flow<List<Medication>> = medicationDao.searchMedications(query)

    fun getMedicationsByCategory(category: String): Flow<List<Medication>> = medicationDao.getMedicationsByCategory(category)

    suspend fun getAlternatives(activeIngredient: String, currentId: String): List<Medication> = medicationDao.getAlternatives(activeIngredient, currentId)

    fun getAllCategories(): Flow<List<String>> = medicationDao.getAllCategories()

    suspend fun insertMedication(medication: Medication) = medicationDao.insertMedication(medication)

    suspend fun deleteMedication(medication: Medication) = medicationDao.deleteMedication(medication)

    fun getRecentScans(): Flow<List<ScanHistory>> = scanHistoryDao.getRecentScans()

    suspend fun saveScanHistory(scan: ScanHistory) = scanHistoryDao.insert(scan)

    suspend fun deleteScanHistory(scan: ScanHistory) = scanHistoryDao.delete(scan)
}
