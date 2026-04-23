package com.farmacode.backend.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta con los datos de un medicamento formateados para el cliente Android.
 * Los nombres de campo coinciden con la entidad Medication del frontend.
 *
 * @param id                 identificador único del medicamento
 * @param nombre             nombre comercial del medicamento
 * @param principioActivo    nombre del principio activo asociado
 * @param categoriaTerapeutica categoría terapéutica del principio activo (ej: "Analgésico")
 * @param laboratorio        nombre del laboratorio fabricante
 * @param paisOrigen         país de origen del laboratorio
 * @param dosis              dosis del medicamento (ej: "500mg")
 * @param presentacion       forma de presentación (ej: "Comprimidos x20")
 * @param administracion     vía de administración (ej: "Oral")
 * @param tipo               clasificación: "Genérico", "Referencia" o "Bioequivalente"
 * @param certificacionISP   true si cuenta con certificación ISP Chile
 * @param descripcion        descripción adicional del producto
 * @param precioActual       precio vigente en pesos chilenos (CLP), puede ser nulo
 */
public record MedicamentoResponseDTO(
        Long id,
        String nombre,
        String principioActivo,
        String categoriaTerapeutica,
        String laboratorio,
        String paisOrigen,
        String dosis,
        String presentacion,
        String administracion,
        String tipo,
        Boolean certificacionISP,
        String descripcion,
        BigDecimal precioActual
) {}
