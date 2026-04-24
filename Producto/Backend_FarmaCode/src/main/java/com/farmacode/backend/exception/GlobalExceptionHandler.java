package com.farmacode.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la API REST de FarmaCode.
 * Centraliza el manejo de errores y garantiza respuestas consistentes en formato JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja recursos no encontrados y responde con HTTP 404.
     *
     * @param ex excepción con el mensaje del recurso no encontrado
     * @return respuesta JSON con código 404 y descripción del error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * Maneja errores de validación de campos (@Valid / @Validated) y responde con HTTP 400.
     * Incluye la lista de campos inválidos con sus mensajes de error.
     *
     * @param ex excepción generada por Bean Validation
     * @return respuesta JSON con código 400 y lista de errores por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación en los datos de entrada", errors);
    }

    /**
     * Maneja errores de la Claude API y responde con HTTP 502 Bad Gateway.
     *
     * @param ex excepción con el detalle del error de la API externa
     * @return respuesta JSON con código 502
     */
    @ExceptionHandler(ClaudeApiException.class)
    public ResponseEntity<Map<String, Object>> handleClaudeApiError(ClaudeApiException ex) {
        return buildResponse(HttpStatus.BAD_GATEWAY, "Error al comunicarse con la API de IA: " + ex.getMessage(), null);
    }

    /**
     * Maneja cualquier excepción no controlada y responde con HTTP 500.
     *
     * @param ex excepción inesperada
     * @return respuesta JSON con código 500 y mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno en el servidor", null);
    }

    /**
     * Construye el cuerpo de respuesta de error en formato JSON estándar.
     *
     * @param status  código HTTP de la respuesta
     * @param message mensaje descriptivo del error
     * @param errors  lista adicional de errores (puede ser nula)
     * @return ResponseEntity con el mapa de error
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, List<String> errors) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (errors != null) {
            body.put("errors", errors);
        }
        return ResponseEntity.status(status).body(body);
    }
}
