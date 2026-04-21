package com.farmacode.backend.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta con los datos de un medicamento formateados para el cliente Android.
 * Incluye datos del principio activo, laboratorio, precio vigente y clasificación ISP.
 *
 * @param id              identificador único del medicamento
 * @param nombreComercial nombre de marca del medicamento
 * @param principioActivo nombre del principio activo asociado
 * @param laboratorio     nombre del laboratorio fabricante
 * @param pais            país de origen del laboratorio
 * @param dosis           dosis del medicamento (ej: "500mg")
 * @param presentacion    forma de presentación (ej: "Comprimidos x20")
 * @param administracion  vía de administración (ej: "Oral")
 * @param tipo            clasificación: GENERICO, REFERENCIA o BIOEQUIVALENTE
 * @param certIsp         true si cuenta con certificación ISP Chile
 * @param descripcion     descripción adicional del producto
 * @param precioActual    precio vigente en pesos chilenos (CLP), puede ser nulo
 */
public record MedicamentoResponseDTO(
        Long id,
        String nombreComercial,
        String principioActivo,
        String laboratorio,
        String pais,
        String dosis,
        String presentacion,
        String administracion,
        String tipo,
        Boolean certIsp,
        String descripcion,
        BigDecimal precioActual
) {}
