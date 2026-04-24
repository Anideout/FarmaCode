package com.farmacode.backend.dto.response;

import java.util.List;

/**
 * DTO de respuesta principal del flujo de búsqueda de FarmaCode.
 * Agrupa el principio activo identificado por Claude API junto con la lista
 * de todos los medicamentos bioequivalentes disponibles, ordenados por precio ascendente.
 *
 * @param principioActivo nombre del principio activo identificado (ej: "Paracetamol")
 * @param categoria       categoría terapéutica del principio activo (ej: "Analgésico")
 * @param medicamentos    lista de medicamentos con ese principio activo, ordenados por precio
 */
public record BioequivalentesResponseDTO(
        String principioActivo,
        String categoria,
        List<MedicamentoResponseDTO> medicamentos
) {}
