package com.farmacox.farmacode.repository

import com.farmacox.farmacode.TestData
import com.farmacox.farmacode.data.dao.UserDao
import com.farmacox.farmacode.data.dao.entity.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {

    @Mock
    private lateinit var userDao: UserDao

    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        repository = UserRepository(userDao)
    }

    @Test
    fun `getUserByEmail returns user from DAO`() = runTest {
        whenever(userDao.getUserByEmail("test@example.com")).thenReturn(TestData.testUser)

        val result = repository.getUserByEmail("test@example.com")

        assertEquals(TestData.testUser, result)
        verify(userDao).getUserByEmail("test@example.com")
    }

    @Test
    fun `getUserByEmail returns null when user not found`() = runTest {
        whenever(userDao.getUserByEmail("unknown@example.com")).thenReturn(null)

        val result = repository.getUserByEmail("unknown@example.com")

        assertNull(result)
        verify(userDao).getUserByEmail("unknown@example.com")
    }

    @Test
    fun `insertUser delegates to DAO`() = runTest {
        val newUser = User(name = "New", email = "new@example.com", password = "123456")

        repository.insertUser(newUser)

        verify(userDao).insertUser(newUser)
    }
}
