package com.farmacox.farmacode.repository

import com.farmacox.farmacode.TestData
import com.farmacox.farmacode.data.network.FarmaCodeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MedicationRepositoryTest {

    @Mock
    private lateinit var apiService: FarmaCodeApiService

    private lateinit var repository: MedicationRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = MedicationRepository(apiService)
    }

    @Test
    fun `getAllMedication returns mapped medications`() = runTest(testDispatcher) {
        val pageResponse = TestData.medicamentoPageResponse()
        whenever(apiService.getAllMedicamentos(size = 1000)).thenReturn(pageResponse)

        val result = repository.getAllMedication().let { flow ->
            val items = mutableListOf<List<com.farmacox.farmacode.data.model.Medication>>()
            flow.collect { items.add(it) }
            items
        }

        assertEquals(1, result.size)
        assertEquals(2, result[0].size)
        assertEquals("Paracetamol", result[0][0].nombre)
        assertEquals("Ibuprofeno", result[0][1].nombre)
    }

    @Test
    fun `getMedicationById returns medication`() = runTest {
        val response = TestData.paracetamolResponse()
        whenever(apiService.getMedicamentoById(1L)).thenReturn(response)

        val result = repository.getMedicationById("1")

        assertNotNull(result)
        assertEquals("Paracetamol", result!!.nombre)
    }

    @Test
    fun `searchMedications returns mapped list`() = runTest(testDispatcher) {
        val searchResults = listOf(TestData.paracetamolResponse())
        whenever(apiService.searchMedicamentos("Paracetamol")).thenReturn(searchResults)

        val result = repository.searchMedications("Paracetamol").let { flow ->
            val items = mutableListOf<List<com.farmacox.farmacode.data.model.Medication>>()
            flow.collect { items.add(it) }
            items
        }

        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
    }

    @Test
    fun `getAlternatives returns list without current id`() = runTest {
        val alternatives = listOf(TestData.ibuprofenoResponse()) // ID is 2
        whenever(apiService.getMedicamentosByPrincipioActivo("Paracetamol")).thenReturn(alternatives)

        // Usamos ID "1" para que NO se filtre el Ibuprofeno (ID "2")
        val result = repository.getAlternatives("Paracetamol", "1")

        assertEquals(1, result.size)
        assertEquals("Ibuprofeno", result[0].nombre)
    }

    @Test
    fun `getAlternatives excludes current medication`() = runTest {
        val currentResponse = TestData.paracetamolResponse() // ID is 1
        val altResponse = TestData.ibuprofenoResponse() // ID is 2
        whenever(apiService.getMedicamentosByPrincipioActivo("Cualquiera"))
            .thenReturn(listOf(currentResponse, altResponse))

        // Excluimos el ID "1"
        val result = repository.getAlternatives("Cualquiera", "1")

        assertEquals(1, result.size)
        assertEquals("2", result[0].id)
    }

    @Test
    fun `getAllCategories returns distinct list`() = runTest(testDispatcher) {
        val allMeds = listOf(TestData.paracetamolResponse(), TestData.ibuprofenoResponse())
        whenever(apiService.getAllMedicamentos(size = 1000)).thenReturn(
            TestData.medicamentoPageResponse().copy(content = allMeds)
        )

        val result = repository.getAllCategories().let { flow ->
            val items = mutableListOf<List<String>>()
            flow.collect { items.add(it) }
            items
        }

        assertTrue(result[0].contains("Analgésicos"))
        assertTrue(result[0].contains("Antiinflamatorios"))
    }
}
