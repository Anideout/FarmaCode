package com.farmacox.farmacode.data.dao.entity

data class Medication(
    val id: String,
    val nombre: String,
    val principioActivo: String,
    val dosis: String,
    val presentacion: String,
    val laboratorio: String,
    val paisOrigen: String,
    val tipo: String,
    val categoriaTerapeutica: String,
    val certificacionISP: Boolean,
    val descripcion: String
)
