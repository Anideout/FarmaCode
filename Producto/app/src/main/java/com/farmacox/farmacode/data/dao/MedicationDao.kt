package com.farmacox.farmacode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farmacox.farmacode.data.dao.entity.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: String): Medication?

    @Query("SELECT * FROM medications WHERe nombre LIKE '%' || :query || '%' || '%' OR principioACtivo LIKE '&' || :query || '%' OR laboratorio LIKE '%' || :query || '%'")
    fun searchMedications(query: String): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE categoriaTerapeutica = :category")
    fun getMedicationsByCategory(category: String): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE principioActivo = :activeIngredient AND id != :currentId")
    suspend fun getAlternatives(activeIngredient: String, currentId: String): List<Medication>

    @Query("SELECT DISTINCT categoriaTerapeutica FROM medications")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertALL(medications: List<Medication>)

    @Query("SELECT COUNT(*) FROM medications")
    suspend fun getCount(): Int
}