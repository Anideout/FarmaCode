package com.farmacode.backend.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa un ítem del historial de búsquedas del usuario.
 *
 * @param id                       identificador único del registro de historial
 * @param terminoBusqueda          texto que el usuario ingresó o que se extrajo por OCR
 * @param tipoBusqueda             origen de la búsqueda: "MANUAL" u "OCR"
 * @param resultadoPrincipioActivo principio activo identificado por Claude API
 * @param resultadosEncontrados    cantidad de medicamentos bioequivalentes encontrados
 * @param fecha                    fecha y hora en que se realizó la búsqueda
 */
public record HistorialBusquedaDTO(
        Long id,
        String terminoBusqueda,
        String tipoBusqueda,
        String resultadoPrincipioActivo,
        Integer resultadosEncontrados,
        LocalDateTime fecha
) {}
