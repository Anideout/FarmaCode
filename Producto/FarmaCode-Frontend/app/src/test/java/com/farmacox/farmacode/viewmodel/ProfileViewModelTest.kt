package com.farmacox.farmacode.viewmodel

import com.farmacox.farmacode.TestData
import com.farmacox.farmacode.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
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
class ProfileViewModelTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Ensure session is clean before each test
        UserSession.userEmail = null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        UserSession.userEmail = null
    }

    @Test
    fun `init with no session loads empty data`() {
        viewModel = ProfileViewModel(userRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.userName)
        assertEquals("", state.email)
    }

    @Test
    fun `init with session loads user data`() = runTest {
        UserSession.userEmail = "test@example.com"
        whenever(userRepository.getUserByEmail("test@example.com")).thenReturn(TestData.testUser)

        viewModel = ProfileViewModel(userRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Test User", state.userName)
        assertEquals("test@example.com", state.email)
    }

    @Test
    fun `init with session but user not found keeps empty data`() = runTest {
        UserSession.userEmail = "unknown@example.com"
        whenever(userRepository.getUserByEmail("unknown@example.com")).thenReturn(null)

        viewModel = ProfileViewModel(userRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.userName)
        assertEquals("", state.email)
    }

    @Test
    fun `toggleNotificacions enables and disables`() {
        viewModel = ProfileViewModel(userRepository)

        assertTrue(viewModel.uiState.value.isNotificacionsEnabled)

        viewModel.toggleNotificacions(false)
        assertFalse(viewModel.uiState.value.isNotificacionsEnabled)

        viewModel.toggleNotificacions(true)
        assertTrue(viewModel.uiState.value.isNotificacionsEnabled)
    }

    @Test
    fun `toggleSettingsCard toggles visibility`() {
        viewModel = ProfileViewModel(userRepository)

        assertFalse(viewModel.uiState.value.showSettingsCard)

        viewModel.toggleSettingsCard()
        assertTrue(viewModel.uiState.value.showSettingsCard)

        viewModel.toggleSettingsCard()
        assertFalse(viewModel.uiState.value.showSettingsCard)
    }

    @Test
    fun `default values are correct`() {
        viewModel = ProfileViewModel(userRepository)

        val state = viewModel.uiState.value
        assertTrue(state.isNotificacionsEnabled)
        assertFalse(state.isDarkMode)
        assertFalse(state.showSettingsCard)
        assertEquals(16f, state.fontSize, 0.001f)
        assertEquals("Español", state.language)
    }

    @Test
    fun `Factory creates ProfileViewModel`() {
        val factory = ProfileViewModel.Factory(userRepository)
        val created = factory.create(ProfileViewModel::class.java)

        assertNotNull(created)
        assertTrue(created is ProfileViewModel)
    }
}
