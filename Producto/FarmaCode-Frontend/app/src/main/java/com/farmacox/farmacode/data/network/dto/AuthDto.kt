package com.farmacox.farmacode.data.network.dto

data class RegisterRequest(val nombre: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val id: Long, val nombre: String, val email: String)
