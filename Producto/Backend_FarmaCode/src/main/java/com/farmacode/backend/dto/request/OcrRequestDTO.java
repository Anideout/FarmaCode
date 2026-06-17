package com.farmacode.backend.dto.request;

/**
 * DTO de entrada para búsqueda a partir de texto extraído por OCR.
 * Usado en el endpoint POST /api/busqueda/ocr.
 *
 * @param textoOcr     texto crudo extraído de la fotografía (puede estar vacío si el OCR falló)
 * @param imagenBase64 imagen original en Base64 JPEG — usada como fallback cuando el OCR es pobre
 */
public record OcrRequestDTO(
        String textoOcr,
        String imagenBase64
) {}
