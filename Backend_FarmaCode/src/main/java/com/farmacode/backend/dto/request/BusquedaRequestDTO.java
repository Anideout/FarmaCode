package com.farmacode.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para búsqueda por nombre comercial de medicamento.
 * Usado en el endpoint POST /api/busqueda/nombre-comercial.
 *
 * @param nombreComercial nombre comercial del medicamento a buscar (ej: "Aspirina", "Tapsin")
 */
public record BusquedaRequestDTO(
        @NotBlank(message = "El nombre comercial es obligatorio")
        String nombreComercial
) {}
