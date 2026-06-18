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
class HomeViewModelTest {

    @Mock
    private lateinit var repository: MedicationRepository

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        whenever(repository.getAllMedication()).thenReturn(flowOf(TestData.medicationList))
        whenever(repository.getAllCategories()).thenReturn(flowOf(TestData.categoryList))

        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads medications and categories`() {
        val state = viewModel.uiState.value

        assertEquals(TestData.medicationList, state.medications)
        assertEquals(listOf("Todos") + TestData.categoryList, state.categories)
        assertFalse(state.isLoading)
        assertNull(state.selectedCategory)
    }

    @Test
    fun `onSearchQueryChange with blank query reloads all medications`() {
        whenever(repository.getAllMedication()).thenReturn(flowOf(TestData.medicationList))
        viewModel.onSearchQueryChange("")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TestData.medicationList, state.medications)
    }

    @Test
    fun `onSearchQueryChange with query calls searchMedications`() {
        val searchQuery = "Paracetamol"
        val searchResults = listOf(TestData.paracetamol)
        whenever(repository.searchMedications(searchQuery)).thenReturn(flowOf(searchResults))

        viewModel.onSearchQueryChange(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(searchResults, state.medications)
        assertEquals(searchQuery, state.searchQuery)
    }

    @Test
    fun `onCategorySelected with null loads all medications`() {
        whenever(repository.getAllMedication()).thenReturn(flowOf(TestData.medicationList))

        viewModel.onCategorySelected(null)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TestData.medicationList, state.medications)
        assertNull(state.selectedCategory)
    }

    @Test
    fun `onCategorySelected with Todos loads all medications`() {
        whenever(repository.getAllMedication()).thenReturn(flowOf(TestData.medicationList))

        viewModel.onCategorySelected("Todos")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TestData.medicationList, state.medications)
        assertEquals("Todos", state.selectedCategory)
    }

    @Test
    fun `onCategorySelected with specific category filters medications`() {
        val filteredList = listOf(TestData.paracetamol)
        whenever(repository.getMedicationsByCategory("Analgésicos")).thenReturn(flowOf(filteredList))

        viewModel.onCategorySelected("Analgésicos")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(filteredList, state.medications)
        assertEquals("Analgésicos", state.selectedCategory)
    }

    @Test
    fun `onMedicationSelected loads alternatives`() = runTest {
        val alternatives = listOf(TestData.ibuprofeno)
        whenever(repository.getAlternatives("Paracetamol", "1")).thenReturn(alternatives)

        viewModel.onMedicationSelected(TestData.paracetamol)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TestData.paracetamol, state.selectedMedication)
        assertEquals(alternatives, state.alternatives)
    }

    @Test
    fun `onDismissDialog clears selection and alternatives`() = runTest {
        // First select a medication
        whenever(repository.getAlternatives("Paracetamol", "1")).thenReturn(emptyList())
        viewModel.onMedicationSelected(TestData.paracetamol)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then dismiss
        viewModel.onDismissDialog()

        val state = viewModel.uiState.value
        assertNull(state.selectedMedication)
        assertTrue(state.alternatives.isEmpty())
    }

    @Test
    fun `toggleTheme toggles dark mode`() {
        val initialState = viewModel.uiState.value.isDarkTheme

        viewModel.toggleTheme()
        assertEquals(!initialState, viewModel.uiState.value.isDarkTheme)

        viewModel.toggleTheme()
        assertEquals(initialState, viewModel.uiState.value.isDarkTheme)
    }

    @Test
    fun `Factory creates HomeViewModel with correct repository`() {
        val factory = HomeViewModel.Factory(repository)
        val created = factory.create(HomeViewModel::class.java)

        assertNotNull(created)
        assertTrue(created is HomeViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Factory throws for unknown class`() {
        val factory = HomeViewModel.Factory(repository)
        factory.create(LoginViewModel::class.java)
    }
}
