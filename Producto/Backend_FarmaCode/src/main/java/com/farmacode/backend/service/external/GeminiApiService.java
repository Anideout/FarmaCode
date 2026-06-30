package com.farmacode.backend.service.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmacode.backend.exception.GeminiApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.farmacode.backend.dto.request.ChatRequestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
public class GeminiApiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiApiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Información estructurada extraída de un texto OCR por Gemini.
     */
    public record InfoMedicamento(
            String nombreComercial,
            String principioActivo,
            String dosis,
            String presentacion,
            String laboratorio,
            String paisOrigen,
            String viaAdministracion,
            String descripcionGeneral
    ) {
        public static InfoMedicamento vacia() {
            return new InfoMedicamento("N/D", "N/D", "N/D", "N/D", "N/D", "N/D", "N/D", "N/D");
        }
    }

    /**
     * Identifica el nombre comercial de un medicamento a partir de una imagen Base64.
     */
    public String identificarMedicamento(String imagenBase64) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY no configurada, retornando DESCONOCIDO");
            return "DESCONOCIDO";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", "image/jpeg");
            inlineData.put("data", imagenBase64);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text",
                    "Identifica el nombre comercial del medicamento que aparece en esta imagen. " +
                    "Responde ÚNICAMENTE con el nombre comercial del medicamento, sin explicaciones, " +
                    "sin dosis, sin laboratorio. Solo el nombre. " +
                    "Ejemplo: si ves 'Panadol 500mg', responde 'Panadol'. " +
                    "Si no puedes identificar ningún medicamento, responde exactamente 'DESCONOCIDO'. " +
                    "Si la imagen NO muestra un medicamento (ej: alimento, producto de limpieza, persona, objeto genérico, etc.), " +
                    "responde exactamente 'NO_ES_MEDICAMENTO'.");

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(imagePart, textPart));

            Map<String, Object> thinkingConfig = new HashMap<>();
            thinkingConfig.put("thinkingBudget", 0);
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("thinkingConfig", thinkingConfig);

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", List.of(content));
            payload.put("generationConfig", generationConfig);

            String url = apiUrl + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            String resultado = extraerTextoRespuesta(response.getBody());
            log.info("Gemini identificó medicamento en imagen: '{}'", resultado);
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

    /**
     * Identifica el principio activo de un medicamento a partir de su nombre comercial.
     * Reemplaza ClaudeApiService para el flujo de búsqueda manual.
     */
    public String identificarPrincipioActivo(String nombreComercial) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY no configurada, retornando vacío");
            return "";
        }
        String prompt = String.format(
                "Dado el medicamento comercial '%s', responde ÚNICAMENTE con el nombre del principio activo " +
                "en español, sin explicaciones, sin puntuación al final. " +
                "Ejemplo: si el medicamento es 'Aspirina', responde 'Ácido acetilsalicílico'. " +
                "Si no reconoces el medicamento, responde 'DESCONOCIDO'.",
                nombreComercial);
        try {
            String resultado = llamarGeminiTexto(prompt);
            log.info("Gemini identificó principio activo de '{}': '{}'", nombreComercial, resultado);
            return resultado;
        } catch (Exception ex) {
            log.error("Error al identificar principio activo con Gemini para '{}': {}", nombreComercial, ex.getMessage());
            return "";
        }
    }

    /**
     * Extrae información estructurada del medicamento a partir de texto OCR crudo.
     * Reemplaza la extracción por regex en el flujo OCR.
     */
    public InfoMedicamento extraerInformacionDeOcr(String textoOcr) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY no configurada, retornando info vacía");
            return InfoMedicamento.vacia();
        }
        String prompt = "Analiza el siguiente texto extraído por OCR de un envase de medicamento.\n" +
                "Texto OCR:\n" + textoOcr + "\n\n" +
                "Responde ÚNICAMENTE con un JSON válido con esta estructura exacta (sin markdown, sin comentarios):\n" +
                "{\"nombreComercial\":\"...\",\"principioActivo\":\"...\",\"dosis\":\"...\",\"presentacion\":\"...\",\"laboratorio\":\"...\",\"paisOrigen\":\"...\",\"viaAdministracion\":\"...\",\"descripcionGeneral\":\"...\"}\n" +
                "REGLAS IMPORTANTES:\n" +
                "- PRIMERO: Si el texto NO corresponde a un envase de medicamento (ej: alimento, bebida, producto de limpieza, texto aleatorio, otro producto), " +
                "responde ÚNICAMENTE con: {\"nombreComercial\":\"NO_ES_MEDICAMENTO\",\"principioActivo\":\"NO_ES_MEDICAMENTO\",\"dosis\":\"N/D\",\"presentacion\":\"N/D\",\"laboratorio\":\"N/D\",\"paisOrigen\":\"N/D\",\"viaAdministracion\":\"N/D\",\"descripcionGeneral\":\"N/D\"}\n" +
                "- 'nombreComercial': SOLO el nombre del medicamento, NUNCA el nombre del laboratorio/fabricante ni parte de él. " +
                "Para medicamentos GENÉRICOS (sin marca registrada propia), el nombreComercial DEBE ser el principio activo, no la marca del fabricante. " +
                "Ejemplo CORRECTO: si ves 'ASCEND LABORATORIES AZITROMICINA 500MG' → nombreComercial='Azitromicina', laboratorio='Ascend Laboratories'. " +
                "Ejemplo CORRECTO: 'Levocetirizina Diclorhidrato'. Ejemplo INCORRECTO: 'HETERO Levocetirizina Diclorhidrato' o 'Ascend'.\n" +
                "- 'principioActivo': si no aparece explícitamente, dedúcelo con tu conocimiento farmacológico.\n" +
                "- 'laboratorio': nombre COMPLETO del fabricante (ej: 'Ascend Laboratories', 'HETERO', 'Pfizer'). Nunca solo la segunda palabra.\n" +
                "- 'paisOrigen': OBLIGATORIO — infiere el país del laboratorio con tu conocimiento aunque no aparezca en el texto. " +
                "Ejemplos: 'Ascend Laboratories' → 'India', 'Pfizer' → 'Estados Unidos', 'Bayer' → 'Alemania', " +
                "'HETERO' → 'India', 'Roche' → 'Suiza', 'Laboratorio Chile' → 'Chile', 'Novartis' → 'Suiza', " +
                "'AstraZeneca' → 'Reino Unido'. NUNCA uses N/D si identificaste el laboratorio.\n" +
                "- 'viaAdministracion': usa solo: Oral, Tópica, Intravenosa, Inhalada, Sublingual, N/D.\n" +
                "- 'descripcionGeneral': OBLIGATORIO — escribe 1-2 oraciones sobre el uso terapéutico del medicamento " +
                "usando tu conocimiento farmacológico. Nunca pongas N/D en este campo si conoces el principio activo.\n" +
                "- Usa capitalización normal en todos los campos (primera letra mayúscula, resto minúsculas). NUNCA escribas campos enteros en MAYÚSCULAS.\n" +
                "Usa \"N/D\" solo si es realmente imposible determinarlo.";
        try {
            String respuesta = llamarGeminiTexto(prompt);
            InfoMedicamento info = parsearInfoMedicamento(respuesta);
            log.info("Gemini extrajo de OCR — nombre: '{}', principioActivo: '{}', dosis: '{}'",
                    info.nombreComercial(), info.principioActivo(), info.dosis());
            return info;
        } catch (Exception ex) {
            log.error("Error al extraer información OCR con Gemini: {}", ex.getMessage());
            return InfoMedicamento.vacia();
        }
    }

    /**
     * Extrae información estructurada del medicamento directamente desde una imagen Base64.
     * Usado como fallback cuando el texto OCR es insuficiente.
     */
    public InfoMedicamento extraerInformacionDeImagen(String imagenBase64) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY no configurada, retornando info vacía");
            return InfoMedicamento.vacia();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", "image/jpeg");
            inlineData.put("data", imagenBase64);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text",
                    "Analiza esta imagen de un envase de medicamento y extrae la información estructurada.\n" +
                    "Responde ÚNICAMENTE con un JSON válido con esta estructura exacta (sin markdown, sin comentarios):\n" +
                    "{\"nombreComercial\":\"...\",\"principioActivo\":\"...\",\"dosis\":\"...\",\"presentacion\":\"...\",\"laboratorio\":\"...\",\"paisOrigen\":\"...\",\"viaAdministracion\":\"...\",\"descripcionGeneral\":\"...\"}\n" +
                    "REGLAS IMPORTANTES:\n" +
                    "- PRIMERO: Si la imagen NO muestra un medicamento (ej: alimento, bebida, producto de limpieza, persona, objeto genérico, etc.), " +
                    "responde ÚNICAMENTE con: {\"nombreComercial\":\"NO_ES_MEDICAMENTO\",\"principioActivo\":\"NO_ES_MEDICAMENTO\",\"dosis\":\"N/D\",\"presentacion\":\"N/D\",\"laboratorio\":\"N/D\",\"paisOrigen\":\"N/D\",\"viaAdministracion\":\"N/D\",\"descripcionGeneral\":\"N/D\"}\n" +
                    "- 'nombreComercial': SOLO el nombre del medicamento, NUNCA el nombre del laboratorio/fabricante ni parte de él. " +
                    "Para medicamentos GENÉRICOS (sin marca registrada propia), el nombreComercial DEBE ser el principio activo, no la marca del fabricante. " +
                    "Ejemplo CORRECTO: si ves 'ASCEND LABORATORIES AZITROMICINA 500MG' → nombreComercial='Azitromicina', laboratorio='Ascend Laboratories'. " +
                    "Ejemplo CORRECTO: 'Levocetirizina Diclorhidrato'. Ejemplo INCORRECTO: 'HETERO Levocetirizina Diclorhidrato' o 'Ascend'.\n" +
                    "- 'laboratorio': nombre COMPLETO del fabricante (ej: 'Ascend Laboratories', 'HETERO', 'Pfizer'). Nunca solo la segunda palabra.\n" +
                    "- 'paisOrigen': OBLIGATORIO — infiere el país del laboratorio con tu conocimiento aunque no aparezca en la imagen. " +
                    "Ejemplos: 'Ascend Laboratories' → 'India', 'Pfizer' → 'Estados Unidos', 'Bayer' → 'Alemania', " +
                    "'HETERO' → 'India', 'Roche' → 'Suiza', 'Laboratorio Chile' → 'Chile', 'Novartis' → 'Suiza', " +
                    "'AstraZeneca' → 'Reino Unido'. NUNCA uses N/D si identificaste el laboratorio.\n" +
                    "- 'viaAdministracion': usa solo: Oral, Tópica, Intravenosa, Inhalada, Sublingual, N/D.\n" +
                    "- 'descripcionGeneral': OBLIGATORIO — escribe 1-2 oraciones sobre el uso terapéutico del medicamento " +
                    "usando tu conocimiento farmacológico. Nunca pongas N/D en este campo si conoces el principio activo.\n" +
                    "- Usa capitalización normal en todos los campos (primera letra mayúscula, resto minúsculas). NUNCA escribas campos enteros en MAYÚSCULAS.\n" +
                    "Usa \"N/D\" solo si es realmente imposible determinarlo.");

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(imagePart, textPart));

            Map<String, Object> thinkingConfig = new HashMap<>();
            thinkingConfig.put("thinkingBudget", 0);
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("thinkingConfig", thinkingConfig);

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", List.of(content));
            payload.put("generationConfig", generationConfig);

            String url = apiUrl + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            String respuesta = extraerTextoRespuesta(response.getBody());
            InfoMedicamento info = parsearInfoMedicamento(respuesta);
            log.info("Gemini Vision extrajo — nombre: '{}', principioActivo: '{}', dosis: '{}'",
                    info.nombreComercial(), info.principioActivo(), info.dosis());
            return info;
        } catch (Exception ex) {
            log.error("Error al extraer información de imagen con Gemini Vision: {}", ex.getMessage());
            return InfoMedicamento.vacia();
        }
    }

    /**
     * Conversación multi-turno con Gemini restringida a farmacología.
     */
    public String chatFarmaceutico(List<ChatRequestDTO.TurnoChat> historial, String nuevoMensaje, String systemPrompt) {
        if (apiKey == null || apiKey.isBlank()) return "Error: API key no configurada.";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> sysPart = new HashMap<>();
            sysPart.put("text", systemPrompt);
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", List.of(sysPart));

            List<Map<String, Object>> contents = new ArrayList<>();
            if (historial != null) {
                for (ChatRequestDTO.TurnoChat turno : historial) {
                    Map<String, Object> part = new HashMap<>();
                    part.put("text", turno.contenido());
                    Map<String, Object> content = new HashMap<>();
                    content.put("role", turno.rol());
                    content.put("parts", List.of(part));
                    contents.add(content);
                }
            }

            Map<String, Object> newPart = new HashMap<>();
            newPart.put("text", nuevoMensaje);
            Map<String, Object> newContent = new HashMap<>();
            newContent.put("role", "user");
            newContent.put("parts", List.of(newPart));
            contents.add(newContent);

            Map<String, Object> thinkingConfig = new HashMap<>();
            thinkingConfig.put("thinkingBudget", 0);
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("thinkingConfig", thinkingConfig);

            Map<String, Object> payload = new HashMap<>();
            payload.put("system_instruction", systemInstruction);
            payload.put("contents", contents);
            payload.put("generationConfig", generationConfig);

            String url = apiUrl + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            return extraerTextoRespuesta(response.getBody());

        } catch (Exception ex) {
            log.error("Error en chatFarmaceutico con Gemini: {}", ex.getMessage());
            return "Lo siento, ocurrió un error al procesar tu consulta. Por favor intenta nuevamente.";
        }
    }

    // ─── Métodos privados ──────────────────────────────────────────────────────

    private String llamarGeminiTexto(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> thinkingConfig = new HashMap<>();
        thinkingConfig.put("thinkingBudget", 0);
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("thinkingConfig", thinkingConfig);

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", List.of(content));
        payload.put("generationConfig", generationConfig);

        String url = apiUrl + "?key=" + apiKey;
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return extraerTextoRespuesta(response.getBody());
    }

    private InfoMedicamento parsearInfoMedicamento(String json) {
        try {
            String limpio = json.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```\\s*", "").trim();
            Map<String, String> map = objectMapper.readValue(limpio, new TypeReference<>() {});
            return new InfoMedicamento(
                    getOrND(map, "nombreComercial"),
                    getOrND(map, "principioActivo"),
                    getOrND(map, "dosis"),
                    getOrND(map, "presentacion"),
                    getOrND(map, "laboratorio"),
                    getOrND(map, "paisOrigen"),
                    getOrND(map, "viaAdministracion"),
                    getOrND(map, "descripcionGeneral")
            );
        } catch (Exception ex) {
            log.error("Error parseando respuesta JSON de Gemini: '{}' — {}", json, ex.getMessage());
            return InfoMedicamento.vacia();
        }
    }

    private String getOrND(Map<String, String> map, String key) {
        String value = map.get(key);
        return (value == null || value.isBlank()) ? "N/D" : value;
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
                        for (Map<String, Object> part : parts) {
                            // Skip thinking parts produced by gemini-2.5-flash
                            if (Boolean.TRUE.equals(part.get("thought"))) continue;
                            Object text = part.get("text");
                            if (text != null) return text.toString().trim();
                        }
                    }
                }
            }
        } catch (ClassCastException ex) {
            log.error("Formato inesperado en respuesta de Gemini API", ex);
        }
        return "DESCONOCIDO";
    }
}
