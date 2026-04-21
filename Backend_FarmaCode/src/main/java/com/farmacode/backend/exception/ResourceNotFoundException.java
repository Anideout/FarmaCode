package com.farmacode.backend.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la base de datos.
 * El {@link GlobalExceptionHandler} la captura y responde con HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea una excepción con un mensaje descriptivo del recurso no encontrado.
     *
     * @param message descripción del recurso que no fue encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Crea una excepción con mensaje y causa raíz.
     *
     * @param message descripción del recurso que no fue encontrado
     * @param cause   excepción original que causó este error
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
