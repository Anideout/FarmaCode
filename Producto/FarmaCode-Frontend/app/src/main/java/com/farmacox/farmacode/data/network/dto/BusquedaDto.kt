package com.farmacox.farmacode.data.network.dto

data class ChatTurn(val rol: String, val contenido: String)
data class ChatRequest(val mensaje: String, val historial: List<ChatTurn>)
data class ChatResponse(val respuesta: String)

data class FotoRequest(val imagenBase64: String)

data class BioequivalentesResponse(
    val principioActivo: String,
    val categoria: String?,
    val medicamentos: List<MedicamentoResponse>
)

data class OcrRequest(val textoOcr: String, val imagenBase64: String)
