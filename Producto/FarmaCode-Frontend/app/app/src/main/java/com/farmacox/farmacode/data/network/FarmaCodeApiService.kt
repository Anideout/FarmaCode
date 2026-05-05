package com.farmacox.farmacode.data.network

import com.farmacox.farmacode.data.network.dto.BioequivalentesResponse
import com.farmacox.farmacode.data.network.dto.FotoRequest
import com.farmacox.farmacode.data.network.dto.MedicamentoResponse
import com.farmacox.farmacode.data.network.dto.PageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FarmaCodeApiService {

    @GET("api/medicamentos")
    suspend fun getAllMedicamentos(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): PageResponse<MedicamentoResponse>

    @GET("api/medicamentos/{id}")
    suspend fun getMedicamentoById(@Path("id") id: Long): MedicamentoResponse

    @GET("api/medicamentos/buscar")
    suspend fun searchMedicamentos(@Query("nombre") nombre: String): List<MedicamentoResponse>

    @GET("api/medicamentos/principio-activo/{nombre}")
    suspend fun getMedicamentosByPrincipioActivo(
        @Path("nombre") nombre: String
    ): List<MedicamentoResponse>

    @POST("api/busqueda/foto")
    suspend fun buscarPorFoto(@Body request: FotoRequest): BioequivalentesResponse
}
