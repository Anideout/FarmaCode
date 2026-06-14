package com.farmacox.farmacode.data.network

import com.farmacox.farmacode.data.network.dto.BioequivalentesResponse
import com.farmacox.farmacode.data.network.dto.ChatRequest
import com.farmacox.farmacode.data.network.dto.ChatResponse
import com.farmacox.farmacode.data.network.dto.OcrRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface BusquedaApiService {

    @POST("api/busqueda/ocr")
    suspend fun buscarPorOcr(@Body request: OcrRequest): BioequivalentesResponse

    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
