package com.farmacox.farmacode.data.network.dto

data class FotoRequest(val imagenBase64: String)

data class BioequivalentesResponse(
    val principioActivo: String,
    val categoria: String?,
    val medicamentos: List<MedicamentoResponse>
)
