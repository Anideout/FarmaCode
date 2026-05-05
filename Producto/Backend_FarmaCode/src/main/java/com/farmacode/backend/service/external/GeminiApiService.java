package com.farmacode.backend.service.external;

import com.farmacode.backend.exception.GeminiApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Servicio que llama a la Gemini Vision API de Google para identificar
 * el nombre comercial de un medicamento a partir de una imagen en Base64.
 *
 * Modelo usado: gemini-1.5-flash (gratuito con cuota generosa)
 * Endpoint: POST https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=API_KEY
 */
@Slf4j
@Service
public class GeminiApiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiApiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Envía una imagen en Base64 a Gemini Vision y retorna el nombre comercial
     * del medicamento identificado, o "DESCONOCIDO" si no se puede identificar.
     *
     * @param imagenBase64 imagen del medicamento codificada en Base64 (JPEG)
     * @return nombre comercial del medicamento identificado
     * @throws GeminiApiException si ocurre un error de comunicación con la API
     */
    public String identificarMedicamento(String imagenBase64) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY no configurada, retornando DESCONOCIDO");
            return "DESCONOCIDO";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Parte de la imagen
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", "image/jpeg");
            inlineData.put("data", imagenBase64);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);

            // Parte del texto (prompt)
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text",
                    "Identifica el nombre comercial del medicamento que aparece en esta imagen. " +
                    "Responde ÚNICAMENTE con el nombre comercial del medicamento, sin explicaciones, " +
                    "sin dosis, sin laboratorio. Solo el nombre. " +
                    "Ejemplo: si ves 'Panadol 500mg', responde 'Panadol'. " +
                    "Si no puedes identificar ningún medicamento, responde exactamente 'DESCONOCIDO'.");

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(imagePart, textPart));

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", List.of(content));

            // La API key va como query param
            String url = apiUrl + "?key=" + apiKey;

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            String resultado = extraerTextoRespuesta(response.getBody());
            log.info("Gemini identificó medicamento: '{}'", resultado);
            return resultado;

        } catch (HttpStatusCodeException ex) {
            String body = ex.getResponseBodyAsString();
            log.error("Gemini API respondió con error {}: {}", ex.getStatusCode(), body);
            throw new GeminiApiException("Gemini API " + ex.getStatusCode() + ": " + body, ex);
        } catch (RestClientException ex) {
            log.error("RestClientException llamando a Gemini API: {}", ex.getMessage(), ex);
            throw new GeminiApiException("Error de comunicación con Gemini API: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Excepción inesperada llamando a Gemini API: {}", ex.getMessage(), ex);
            throw new GeminiApiException("Error inesperado al llamar a Gemini API: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private String extraerTextoRespuesta(Map<?, ?> body) {
        if (body == null) return "DESCONOCIDO";
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> firstCandidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                if (content != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        Object text = parts.get(0).get("text");
                        return text != null ? text.toString().trim() : "DESCONOCIDO";
                    }
                }
            }
        } catch (ClassCastException ex) {
            log.error("Formato inesperado en respuesta de Gemini API", ex);
        }
        return "DESCONOCIDO";
    }
}
