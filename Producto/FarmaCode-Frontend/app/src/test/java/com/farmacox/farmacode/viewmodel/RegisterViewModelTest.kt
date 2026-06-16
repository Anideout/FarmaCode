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
class RegisterViewModelTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: RegisterViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertFalse(state.isRegisterSuccessful)
    }

    @Test
    fun `onNameChange updates name`() {
        viewModel.onNameChange("Test User")
        assertEquals("Test User", viewModel.uiState.value.name)
    }

    @Test
    fun `onEmailChange updates email`() {
        viewModel.onEmailChange("test@example.com")
        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `onPaswordChange updates password`() {
        viewModel.onPaswordChange("123456")
        assertEquals("123456", viewModel.uiState.value.password)
    }

    @Test
    fun `onConfirmPaswordChange updates confirmPassword`() {
        viewModel.onConfirmPaswordChange("123456")
        assertEquals("123456", viewModel.uiState.value.confirmPassword)
    }

    @Test
    fun `field changes clear error`() {
        // Trigger empty fields error
        viewModel.onRegisterClick()
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Changing field clears error
        viewModel.onNameChange("Test")
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onRegisterClick with empty fields shows error`() {
        viewModel.onRegisterClick()

        val state = viewModel.uiState.value
        assertEquals("Debes completar todos los campos...", state.errorMessage)
        assertFalse(state.isRegisterSuccessful)
    }

    @Test
    fun `onRegisterClick with invalid email shows error`() {
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("invalid-email")
        viewModel.onPaswordChange("123456")
        viewModel.onConfirmPaswordChange("123456")
        viewModel.onRegisterClick()

        assertEquals("email invalido...", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onRegisterClick with short password shows error`() {
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPaswordChange("123")
        viewModel.onConfirmPaswordChange("123")
        viewModel.onRegisterClick()

        assertEquals(
            "La contraseña debe contener al menos 6 caracteres",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun `onRegisterClick with password mismatch shows error`() {
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPaswordChange("123456")
        viewModel.onConfirmPaswordChange("654321")
        viewModel.onRegisterClick()

        assertEquals("Las contraseñas no coinciden", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onRegisterClick with existing user shows error`() = runTest {
        whenever(userRepository.getUserByEmail("test@example.com")).thenReturn(TestData.testUser)

        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPaswordChange("123456")
        viewModel.onConfirmPaswordChange("123456")
        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("El usuario ya existe", state.errorMessage)
        assertFalse(state.isRegisterSuccessful)
    }

    @Test
    fun `onRegisterClick with valid data succeeds`() = runTest {
        whenever(userRepository.getUserByEmail("new@example.com")).thenReturn(null)

        viewModel.onNameChange("New User")
        viewModel.onEmailChange("new@example.com")
        viewModel.onPaswordChange("123456")
        viewModel.onConfirmPaswordChange("123456")
        viewModel.onRegisterClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isRegisterSuccessful)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `creating user sets loading state`() = runTest {
        whenever(userRepository.getUserByEmail("new@example.com")).thenReturn(null)

        viewModel.onNameChange("New User")
        viewModel.onEmailChange("new@example.com")
        viewModel.onPaswordChange("123456")
        viewModel.onConfirmPaswordChange("123456")

        // Start registration (no advance yet)
        viewModel.onRegisterClick()

        // Should be loading
        assertTrue(viewModel.uiState.value.isLoading)

        // Complete
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `Factory creates RegisterViewModel`() {
        val factory = RegisterViewModel.Factory(userRepository)
        val created = factory.create(RegisterViewModel::class.java)

        assertNotNull(created)
        assertTrue(created is RegisterViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Factory throws for unknown class`() {
        val factory = RegisterViewModel.Factory(userRepository)
        factory.create(LoginViewModel::class.java)
    }
}
