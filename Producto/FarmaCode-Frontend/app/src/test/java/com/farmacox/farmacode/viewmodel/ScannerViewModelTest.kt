package com.farmacox.farmacode.viewmodel

import com.farmacox.farmacode.TestData
import com.farmacox.farmacode.repository.MedicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class ScannerViewModelTest {

    @Mock
    private lateinit var repository: MedicationRepository

    private lateinit var viewModel: ScannerViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScannerViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value

        assertEquals("", state.scannedCode)
        assertNull(state.foundMedication)
        assertFalse(state.isLoading)
        assertFalse(state.showResult)
        assertNull(state.errorMessage)
        assertTrue(state.alternatives.isEmpty())
    }

    @Test
    fun `simulateScan with extended format creates medication from QR`() {
        val qrCode = "Paracetamol|Paracetamol|500mg|Analgésicos|Tabletas|Lab A|Chile|Analgésico y antipirético|Nuevo"

        viewModel.simulateScan(qrCode)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(qrCode.trim(), state.scannedCode)
        assertTrue(state.showResult)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)

        val med = state.foundMedication
        assertNotNull(med)
        assertEquals("Paracetamol", med!!.nombre)
        assertEquals("Paracetamol", med.principioActivo)
        assertEquals("500mg", med.dosis)
        assertEquals("Analgésicos", med.categoriaTerapeutica)
        assertEquals("Tabletas", med.presentacion)
        assertEquals("Lab A", med.laboratorio)
        assertEquals("Chile", med.paisOrigen)
        assertEquals("Analgésico y antipirético", med.descripcion)
        assertEquals("Nuevo", med.tipo)
        assertTrue(med.certificacionISP)
        assertTrue(med.id.startsWith("QR-"))
    }

    @Test
    fun `simulateScan with 4-part format creates medication from old QR`() {
        val qrCode = "Paracetamol|Paracetamol|500mg|Analgésicos"

        viewModel.simulateScan(qrCode)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showResult)

        val med = state.foundMedication
        assertNotNull(med)
        assertEquals("Paracetamol", med!!.nombre)
        assertEquals("Caja estándar", med.presentacion)
        assertEquals("Genérico", med.laboratorio)
        assertEquals("Chile", med.paisOrigen)
        assertEquals("Medicamento agregado por QR.", med.descripcion)
        assertEquals("Nuevo", med.tipo)
        assertTrue(med.certificacionISP)
    }

    @Test
    fun `simulateScan with non-matching format searches API`() = runTest {
        val query = "Paracetamol"
        val searchResults = listOf(TestData.paracetamol)

        whenever(repository.searchMedications(query)).thenReturn(flowOf(searchResults))
        whenever(repository.getAlternatives("Paracetamol", "1")).thenReturn(emptyList())

        viewModel.simulateScan(query)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showResult)
        assertEquals(TestData.paracetamol, state.foundMedication)
    }

    @Test
    fun `simulateScan with no results shows error`() {
        val query = "UnknownMed"

        whenever(repository.searchMedications(query)).thenReturn(flowOf(emptyList()))

        viewModel.simulateScan(query)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.showResult)
        assertNull(state.foundMedication)
        assertEquals("No se encontró el medicamento.", state.errorMessage)
    }

    @Test
    fun `simulateScan with blank code does nothing`() {
        viewModel.simulateScan("")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.showResult)
        assertNull(state.foundMedication)
    }

    @Test
    fun `simulateScan when already loading does nothing`() {
        // First call starts loading
        viewModel.simulateScan("Paracetamol|Paracetamol|500mg|Analgésicos")
        // Don't advance dispatcher - still loading

        // Second call should be ignored
        viewModel.simulateScan("Ibuprofeno|Ibuprofeno|400mg|Antiinflamatorios")
        testDispatcher.scheduler.advanceUntilIdle()

        // Should have processed the first call (Paracetamol), not the second
        val med = viewModel.uiState.value.foundMedication
        assertNotNull(med)
        assertEquals("Paracetamol", med!!.nombre)
    }

    @Test
    fun `dismissResult clears found medication and result`() {
        // First load a result
        viewModel.simulateScan("Paracetamol|Paracetamol|500mg|Analgésicos")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showResult)

        // Then dismiss
        viewModel.dismissResult()

        val state = viewModel.uiState.value
        assertFalse(state.showResult)
        assertNull(state.foundMedication)
    }

    @Test
    fun `searchByText delegates to simulateScan`() = runTest {
        val text = "ibuprofeno"

        whenever(repository.searchMedications(text)).thenReturn(flowOf(listOf(TestData.ibuprofeno)))
        whenever(repository.getAlternatives("Ibuprofeno", "2")).thenReturn(emptyList())

        viewModel.searchByText(text)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showResult)
        assertEquals(TestData.ibuprofeno, state.foundMedication)
    }

    @Test
    fun `setError sets error message`() {
        viewModel.setError("Custom error")

        assertEquals("Custom error", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `Factory creates ScannerViewModel`() {
        val factory = ScannerViewModel.Factory(repository)
        val created = factory.create(ScannerViewModel::class.java)

        assertNotNull(created)
        assertTrue(created is ScannerViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Factory throws for unknown class`() {
        val factory = ScannerViewModel.Factory(repository)
        factory.create(LoginViewModel::class.java)
    }
}
