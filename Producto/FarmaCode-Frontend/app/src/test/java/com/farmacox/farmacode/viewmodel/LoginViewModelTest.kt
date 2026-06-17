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
class LoginViewModelTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertFalse(state.isLoginSuccessful)
    }

    @Test
    fun `onEmailChange updates email and clears error`() {
        viewModel.onEmailChange("test@example.com")

        val state = viewModel.uiState.value
        assertEquals("test@example.com", state.email)
        assertNull(state.errorMessage)
    }

    @Test
    fun `onPasswordChange updates password and clears error`() {
        viewModel.onPasswordChange("123456")

        assertEquals("123456", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onLoginClick with empty fields shows error`() {
        viewModel.onLoginClick()

        val state = viewModel.uiState.value
        assertEquals("Completa todos los campos", state.errorMessage)
        assertFalse(state.isLoginSuccessful)
    }

    @Test
    fun `onLoginClick with empty email shows error`() {
        viewModel.onPasswordChange("123456")
        viewModel.onLoginClick()

        assertEquals("Completa todos los campos", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onLoginClick with invalid email shows error`() {
        viewModel.onEmailChange("not-an-email")
        viewModel.onPasswordChange("123456")
        viewModel.onLoginClick()

        assertEquals("Email inválido", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onLoginClick with valid credentials succeeds`() = runTest {
        whenever(userRepository.getUserByEmail("test@example.com")).thenReturn(TestData.testUser)

        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("123456")
        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLoginSuccessful)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `onLoginClick with wrong password fails`() = runTest {
        val userWithWrongPassword = TestData.testUser.copy(password = "wrongpass")
        whenever(userRepository.getUserByEmail("test@example.com")).thenReturn(userWithWrongPassword)

        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("123456")
        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoginSuccessful)
        assertEquals("Credenciales incorrectas", state.errorMessage)
    }

    @Test
    fun `onLoginClick when user not found fails`() = runTest {
        whenever(userRepository.getUserByEmail("test@example.com")).thenReturn(null)

        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("123456")
        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoginSuccessful)
        assertEquals("Credenciales incorrectas", state.errorMessage)
    }

    @Test
    fun `changing fields after error clears the error`() {
        // Trigger error first
        viewModel.onLoginClick()
        assertEquals("Completa todos los campos", viewModel.uiState.value.errorMessage)

        // Change a field
        viewModel.onEmailChange("test@example.com")
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `Factory creates LoginViewModel`() {
        val factory = LoginViewModel.Factory(userRepository)
        val created = factory.create(LoginViewModel::class.java)

        assertNotNull(created)
        assertTrue(created is LoginViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Factory throws for unknown class`() {
        val factory = LoginViewModel.Factory(userRepository)
        factory.create(HomeViewModel::class.java)
    }
}
