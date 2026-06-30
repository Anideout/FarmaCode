package com.farmacode.backend.exception;

/**
 * Excepción lanzada cuando la Gemini API responde con HTTP 429 (cuota agotada
 * o demasiadas peticiones). Se maneja por separado de {@link GeminiApiException}
 * para poder devolver al cliente un mensaje claro de "servicio saturado" en vez
 * de un error genérico.
 */
public class GeminiRateLimitException extends RuntimeException {

    public GeminiRateLimitException(String message) {
        super(message);
    }
}
