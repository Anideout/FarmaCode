package com.farmacox.farmacode.repository

import com.farmacox.farmacode.data.dao.UserDao
import com.farmacox.farmacode.data.dao.entity.User
import com.farmacox.farmacode.data.network.BusquedaApiService
import com.farmacox.farmacode.data.network.RetrofitClient
import com.farmacox.farmacode.data.network.dto.AuthResponse
import com.farmacox.farmacode.data.network.dto.LoginRequest
import com.farmacox.farmacode.data.network.dto.RegisterRequest

class UserRepository(
    private val userDao: UserDao,
    private val apiService: BusquedaApiService = RetrofitClient.busquedaService
) {
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun registerOnBackend(nombre: String, email: String, password: String): AuthResponse =
        apiService.register(RegisterRequest(nombre, email, password))

    suspend fun loginOnBackend(email: String, password: String): AuthResponse =
        apiService.login(LoginRequest(email, password))
}
