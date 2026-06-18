package com.farmacox.farmacode.viewmodel

import com.farmacox.farmacode.TestData
import com.farmacox.farmacode.repository.MedicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ChatViewModelTest {

    @Mock
    private lateinit var repository: MedicationRepository

    private lateinit var viewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init has welcome message`() {
        val state = viewModel.uiState.value

        assertEquals(1, state.messages.size)
        assertFalse(state.messages[0].isUser)
        assertTrue(state.messages[0].content.contains("Hola"))
        assertFalse(state.isLoading)
    }

    @Test
    fun `sendMessage adds user message and gets response`() {
        viewModel.sendMessage("Hola")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.messages.size) // Welcome + user msg + assistant response
        assertTrue(state.messages[1].isUser)
        assertEquals("Hola", state.messages[1].content)
        assertFalse(state.messages[2].isUser)
        assertTrue(state.messages[2].content.contains("Hola", ignoreCase = true))
        assertFalse(state.isLoading)
    }

    @Test
    fun `sendMessage sets loading state`() {
        viewModel.sendMessage("test")
        // Don't advance - still loading
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `sendMessage with alternativa triggers alternatives response`() {
        viewModel.sendMessage("Necesito una alternativa")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertFalse(lastMessage.isUser)
        assertTrue(lastMessage.content.contains("alternativa", ignoreCase = true))
    }

    @Test
    fun `sendMessage with generico triggers alternatives response`() {
        viewModel.sendMessage("genérico")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("alternativa", ignoreCase = true))
    }

    @Test
    fun `sendMessage with greeting triggers greeting response`() {
        viewModel.sendMessage("buenos días")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("Hola", ignoreCase = true))
    }

    @Test
    fun `sendMessage with hola triggers greeting response`() {
        viewModel.sendMessage("hola")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("Hola", ignoreCase = true))
    }

    @Test
    fun `sendMessage with ISP triggers certification response`() {
        viewModel.sendMessage("Qué es ISP")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("ISP", ignoreCase = true))
        assertTrue(lastMessage.content.contains("Instituto de Salud Pública", ignoreCase = true))
    }

    @Test
    fun `sendMessage with certificacion triggers certification response`() {
        viewModel.sendMessage("certificación")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("ISP", ignoreCase = true))
    }

    @Test
    fun `sendMessage with buscar triggers search`() {
        whenever(repository.searchMedications("paracetamol")).thenReturn(flowOf(TestData.medicationList))

        viewModel.sendMessage("buscar Paracetamol")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("paracetamol", ignoreCase = true))
    }

    @Test
    fun `sendMessage with busca triggers search`() {
        whenever(repository.searchMedications("ibuprofeno")).thenReturn(flowOf(listOf(TestData.ibuprofeno)))

        viewModel.sendMessage("busca ibuprofeno")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("ibuprofeno", ignoreCase = true))
    }

    @Test
    fun `sendMessage with busca but no term asks for input`() {
        viewModel.sendMessage("buscar")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("Indícame", ignoreCase = true))
    }

    @Test
    fun `sendMessage with unknown query triggers default response`() {
        viewModel.sendMessage("qué hora es?")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastMessage = viewModel.uiState.value.messages.last()
        assertTrue(lastMessage.content.contains("no entendí", ignoreCase = true))
    }

    @Test
    fun `Factory creates ChatViewModel`() {
        val factory = ChatViewModel.Factory(repository)
        val created = factory.create(ChatViewModel::class.java)

        assertNotNull(created)
        assertTrue(created is ChatViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Factory throws for unknown class`() {
        val factory = ChatViewModel.Factory(repository)
        factory.create(LoginViewModel::class.java)
    }
}
