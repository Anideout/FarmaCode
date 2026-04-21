package com.farmacode.backend.exception;

/**
 * Excepción lanzada cuando ocurre un error al comunicarse con la Claude API de Anthropic.
 * Puede deberse a timeout, clave inválida, límite de rate, o respuesta inesperada.
 */
public class ClaudeApiException extends RuntimeException {

    /**
     * Crea una excepción con un mensaje descriptivo del error en la Claude API.
     *
     * @param message descripción del error ocurrido
     */
    public ClaudeApiException(String message) {
        super(message);
    }

    /**
     * Crea una excepción con mensaje y causa raíz.
     *
     * @param message descripción del error ocurrido
     * @param cause   excepción original que causó este error
     */
    public ClaudeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
