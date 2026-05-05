package com.farmacox.farmacode.data.network

import com.farmacox.farmacode.data.network.dto.BioequivalentesResponse
import com.farmacox.farmacode.data.network.dto.OcrRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface BusquedaApiService {

    @POST("api/busqueda/ocr")
    suspend fun buscarPorOcr(@Body request: OcrRequest): BioequivalentesResponse
}
