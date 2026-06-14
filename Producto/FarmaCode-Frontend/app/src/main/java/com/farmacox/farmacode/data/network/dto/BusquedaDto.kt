package com.farmacox.farmacode.data.network.dto

data class ChatTurn(val rol: String, val contenido: String)
data class ChatRequest(val mensaje: String, val historial: List<ChatTurn>)
data class ChatResponse(val respuesta: String)

data class FotoRequest(val imagenBase64: String)
data class OcrRequest(
    val textoOcr: String,
    val imagenBase64: String? = null
)

data class BioequivalentesResponse(
    val principioActivo: String,
    val categoria: String?,
    val medicamentos: List<MedicamentoDto>
)

data class MedicamentoDto(
    val id: Long,
    val nombre: String,
    val principioActivo: String?,
    val categoriaTerapeutica: String?,
    val laboratorio: String?,
    val paisOrigen: String?,
    val dosis: String?,
    val presentacion: String?,
    val tipo: String?,
    val certificacionISP: Boolean?,
    val descripcion: String?,
    val precioActual: Double?
)
