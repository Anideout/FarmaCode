package com.farmacode.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para búsqueda a partir de texto extraído por OCR (reconocimiento óptico).
 * Usado en el endpoint POST /api/busqueda/ocr.
 *
 * @param textoOcr texto crudo extraído de la fotografía del medicamento
 */
public record OcrRequestDTO(
        @NotBlank(message = "El texto OCR es obligatorio")
        String textoOcr
) {}
