package com.farmacode.backend.exception;

/**
 * Excepción lanzada cuando ocurre un error al comunicarse con la Gemini API de Google.
 * Puede deberse a timeout, clave inválida, límite de rate, imagen no soportada,
 * o respuesta inesperada.
 */
public class GeminiApiException extends RuntimeException {

    public GeminiApiException(String message) {
        super(message);
    }

    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
