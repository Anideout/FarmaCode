package com.farmacode.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para búsqueda por nombre de medicamento.
 * Usado en el endpoint POST /api/busqueda/nombre-comercial.
 *
 * @param nombre nombre comercial del medicamento a buscar (ej: "Aspirina", "Tapsin")
 */
public record BusquedaRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {}
