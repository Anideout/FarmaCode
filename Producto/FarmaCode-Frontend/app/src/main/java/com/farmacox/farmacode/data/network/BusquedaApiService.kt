package com.farmacox.farmacode.data.network

import com.farmacox.farmacode.data.network.dto.AuthResponse
import com.farmacox.farmacode.data.network.dto.BioequivalentesResponse
import com.farmacox.farmacode.data.network.dto.ChatRequest
import com.farmacox.farmacode.data.network.dto.ChatResponse
import com.farmacox.farmacode.data.network.dto.LoginRequest
import com.farmacox.farmacode.data.network.dto.MedicamentoResponse
import com.farmacox.farmacode.data.network.dto.OcrRequest
import com.farmacox.farmacode.data.network.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BusquedaApiService {

    @POST("api/busqueda/ocr")
    suspend fun buscarPorOcr(@Body request: OcrRequest): BioequivalentesResponse

    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse

    @GET("api/medicamentos/buscar")
    suspend fun buscarMedicamentos(@Query("nombre") nombre: String): List<MedicamentoResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
