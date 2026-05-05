package com.farmacode.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para búsqueda a partir de una fotografía del medicamento.
 * La imagen se envía codificada en Base64 (JPEG, calidad reducida para transferencia).
 * Usado en el endpoint POST /api/busqueda/foto.
 *
 * @param imagenBase64 imagen del medicamento codificada en Base64 sin prefijo data URI
 */
public record FotoRequestDTO(
        @NotBlank(message = "La imagen en base64 es obligatoria")
        String imagenBase64
) {}
