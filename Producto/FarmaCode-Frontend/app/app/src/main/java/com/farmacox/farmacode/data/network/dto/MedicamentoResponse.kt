package com.farmacox.farmacode.data.network.dto

import com.farmacox.farmacode.data.dao.entity.Medication

data class MedicamentoResponse(
    val id: Long,
    val nombre: String,
    val principioActivo: String?,
    val categoriaTerapeutica: String?,
    val laboratorio: String?,
    val paisOrigen: String?,
    val dosis: String,
    val presentacion: String,
    val administracion: String?,
    val tipo: String?,
    val certificacionISP: Boolean,
    val descripcion: String?,
    val precioActual: Double?
) {
    fun toMedication() = Medication(
        id = id.toString(),
        nombre = nombre,
        principioActivo = principioActivo ?: "",
        dosis = dosis,
        presentacion = presentacion,
        laboratorio = laboratorio ?: "",
        paisOrigen = paisOrigen ?: "",
        tipo = tipo ?: "",
        categoriaTerapeutica = categoriaTerapeutica ?: "",
        certificacionISP = certificacionISP,
        descripcion = descripcion ?: ""
    )
}

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int,
    val size: Int
)
