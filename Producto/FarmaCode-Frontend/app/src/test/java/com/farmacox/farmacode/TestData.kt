package com.farmacox.farmacode

import com.farmacox.farmacode.data.dao.entity.User
import com.farmacox.farmacode.data.model.Medication
import com.farmacox.farmacode.data.network.dto.MedicamentoResponse
import com.farmacox.farmacode.data.network.dto.PageResponse

object TestData {

    val paracetamol = Medication(
        id = "1",
        nombre = "Paracetamol",
        principioActivo = "Paracetamol",
        dosis = "500mg",
        presentacion = "Tabletas",
        laboratorio = "Laboratorio A",
        paisOrigen = "Chile",
        tipo = "Analgésico",
        categoriaTerapeutica = "Analgésicos",
        certificacionISP = true,
        descripcion = "Analgésico y antipirético"
    )

    val ibuprofeno = Medication(
        id = "2",
        nombre = "Ibuprofeno",
        principioActivo = "Ibuprofeno",
        dosis = "400mg",
        presentacion = "Cápsulas",
        laboratorio = "Laboratorio B",
        paisOrigen = "Chile",
        tipo = "Antiinflamatorio",
        categoriaTerapeutica = "Antiinflamatorios",
        certificacionISP = true,
        descripcion = "Antiinflamatorio no esteroidal"
    )

    val amoxicilina = Medication(
        id = "3",
        nombre = "Amoxicilina",
        principioActivo = "Amoxicilina",
        dosis = "500mg",
        presentacion = "Cápsulas",
        laboratorio = "Laboratorio C",
        paisOrigen = "Chile",
        tipo = "Antibiótico",
        categoriaTerapeutica = "Antibióticos",
        certificacionISP = true,
        descripcion = "Antibiótico betalactámico"
    )

    val medicationList = listOf(paracetamol, ibuprofeno, amoxicilina)

    val categoryList = listOf("Analgésicos", "Antiinflamatorios", "Antibióticos")

    val testUser = User(
        id = 1,
        name = "Test User",
        email = "test@example.com",
        password = "123456"
    )

    // DTOs para respuestas de API
    fun paracetamolResponse() = MedicamentoResponse(
        id = 1,
        nombre = "Paracetamol",
        principioActivo = "Paracetamol",
        categoriaTerapeutica = "Analgésicos",
        laboratorio = "Laboratorio A",
        paisOrigen = "Chile",
        dosis = "500mg",
        presentacion = "Tabletas",
        administracion = "Oral",
        tipo = "Analgésico",
        certificacionISP = true,
        descripcion = "Analgésico y antipirético",
        precioActual = 2500.0
    )

    fun ibuprofenoResponse() = MedicamentoResponse(
        id = 2,
        nombre = "Ibuprofeno",
        principioActivo = "Ibuprofeno",
        categoriaTerapeutica = "Antiinflamatorios",
        laboratorio = "Laboratorio B",
        paisOrigen = "Chile",
        dosis = "400mg",
        presentacion = "Cápsulas",
        administracion = "Oral",
        tipo = "Antiinflamatorio",
        certificacionISP = true,
        descripcion = "Antiinflamatorio no esteroidal",
        precioActual = 3200.0
    )

    fun medicamentoPageResponse() = PageResponse(
        content = listOf(paracetamolResponse(), ibuprofenoResponse()),
        totalElements = 2,
        totalPages = 1,
        number = 0,
        size = 1000
    )
}
