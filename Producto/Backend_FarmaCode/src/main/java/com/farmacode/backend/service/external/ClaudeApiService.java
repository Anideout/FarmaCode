package com.farmacode.backend.service.external;

import com.farmacode.backend.exception.ClaudeApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio que se comunica con la Claude API de Anthropic para identificar
 * el principio activo de un medicamento a partir de su nombre comercial.
 * Utiliza un prompt estructurado y parsea la respuesta de texto retornada.
 */
@Service
public class ClaudeApiService {

    /** Clave de API de Anthropic, leída desde application.properties */
    @Value("${claude.api.key}")
    private String apiKey;

    /** URL base de la Claude API */
    @Value("${claude.api.url}")
    private String apiUrl;

    /** Modelo de Claude a utilizar para las consultas */
    @Value("${claude.api.model}")
    private String model;

    private final RestTemplate restTemplate;

    /**
     * Constructor que inicializa el RestTemplate con un timeout de 30 segundos
     * para evitar bloqueos indefinidos al llamar a la API externa.
     */
    public ClaudeApiService() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(30_000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Envía el nombre comercial de un medicamento a Claude API y retorna el principio activo identificado.
     * Si la clave de API no está configurada, retorna un valor vacío para permitir pruebas sin IA.
     *
     * @param nombreComercial nombre del medicamento comercial a identificar (ej: "Tapsin", "Aspirina")
     * @return nombre del principio activo identificado por Claude (ej: "Paracetamol")
     * @throws ClaudeApiException si ocurre un error al comunicarse con la API
     */
    public String identificarPrincipioActivo(String nombreComercial) {
        // Si no hay clave configurada, retorna vacío para que el servicio busque en BD
        if (apiKey == null || apiKey.isBlank()) {
            return "";
        }

        try {
            // Construir headers requeridos por Anthropic
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            // Construir el prompt que le pide a Claude identificar el principio activo
            String prompt = String.format(
                    "Dado el medicamento comercial '%s', responde ÚNICAMENTE con el nombre del principio activo " +
                    "en español, sin explicaciones adicionales, sin puntuación al final. " +
                    "Ejemplo: si el medicamento es 'Aspirina', responde 'Ácido acetilsalicílico'. " +
                    "Si no reconoces el medicamento, responde 'DESCONOCIDO'.",
                    nombreComercial
            );

            // Construir el cuerpo del request según la API de Anthropic Messages
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", model);
            payload.put("max_tokens", 100);
            payload.put("messages", List.of(message));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Ejecutar la llamada HTTP POST a la Claude API
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, request, Map.class);

            // Parsear la respuesta: content[0].text
            return extraerTextoRespuesta(response.getBody());

        } catch (RestClientException ex) {
            throw new ClaudeApiException(
                    "Error de comunicación con la Claude API: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new ClaudeApiException(
                    "Error inesperado al llamar a la Claude API: " + ex.getMessage(), ex);
        }
    }

    /**
     * Extrae el texto de la respuesta de la Claude API desde la estructura JSON anidada.
     * La respuesta tiene la forma: {@code { "content": [ { "type": "text", "text": "..." } ] }}
     *
     * @param responseBody mapa con el cuerpo de la respuesta HTTP deserializado
     * @return el texto de la respuesta de Claude, o cadena vacía si no se pudo parsear
     */
    @SuppressWarnings("unchecked")
    private String extraerTextoRespuesta(Map<?, ?> responseBody) {
        if (responseBody == null) {
            return "";
        }
        try {
            List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
            if (content != null && !content.isEmpty()) {
                Object text = content.get(0).get("text");
                return text != null ? text.toString().trim() : "";
            }
        } catch (ClassCastException ex) {
            throw new ClaudeApiException("Formato de respuesta inesperado de la Claude API", ex);
        }
        return "";
    }
}
