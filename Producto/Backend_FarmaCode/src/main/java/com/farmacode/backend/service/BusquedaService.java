package com.farmacode.backend.service;

import com.farmacode.backend.dto.response.BioequivalentesResponseDTO;
import com.farmacode.backend.dto.response.MedicamentoResponseDTO;
import com.farmacode.backend.entity.HistorialBusqueda;
import com.farmacode.backend.entity.Laboratorio;
import com.farmacode.backend.entity.Medicamento;
import com.farmacode.backend.entity.PrincipioActivo;
import com.farmacode.backend.entity.TipoBusqueda;
import com.farmacode.backend.repository.HistorialBusquedaRepository;
import com.farmacode.backend.repository.LaboratorioRepository;
import com.farmacode.backend.repository.MedicamentoRepository;
import com.farmacode.backend.repository.PrincipioActivoRepository;
import com.farmacode.backend.service.external.ClaudeApiService;
import com.farmacode.backend.service.external.GeminiApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Servicio que orquesta el flujo principal de búsqueda de medicamentos bioequivalentes.
 * <p>
 * Flujo:
 * <ol>
 *   <li>Recibe el nombre comercial del medicamento</li>
 *   <li>Busca el medicamento en la BD para obtener su principio activo directamente</li>
 *   <li>Si no lo encuentra en BD, consulta a Claude API para identificar el principio activo</li>
 *   <li>Busca todos los medicamentos con ese principio activo</li>
 *   <li>Obtiene el precio vigente de cada uno y ordena por precio ascendente</li>
 *   <li>Guarda la búsqueda en el historial</li>
 *   <li>Retorna el DTO de respuesta con el listado de bioequivalentes</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusquedaService {

    private final MedicamentoRepository medicamentoRepository;
    private final PrincipioActivoRepository principioActivoRepository;
    private final HistorialBusquedaRepository historialBusquedaRepository;
    private final LaboratorioRepository laboratorioRepository;
    private final ClaudeApiService claudeApiService;
    private final GeminiApiService geminiApiService;
    private final MedicamentoService medicamentoService;

    /**
     * Ejecuta el flujo de búsqueda a partir de un nombre comercial ingresado manualmente.
     *
     * @param nombreComercial nombre del medicamento ingresado por el usuario
     * @return DTO con el principio activo identificado y la lista de bioequivalentes
     */
    @Transactional
    public BioequivalentesResponseDTO buscarPorNombreComercial(String nombreComercial) {
        return ejecutarBusqueda(nombreComercial.trim(), TipoBusqueda.MANUAL);
    }

    /**
     * Ejecuta el flujo de búsqueda a partir de una fotografía del medicamento.
     * Gemini Vision identifica el nombre comercial y luego se aplica el flujo estándar.
     *
     * @param imagenBase64 imagen del medicamento codificada en Base64 (JPEG)
     * @return DTO con el principio activo identificado y la lista de bioequivalentes
     */
    @Transactional
    public BioequivalentesResponseDTO buscarPorFoto(String imagenBase64) {
        String nombreIdentificado = geminiApiService.identificarMedicamento(imagenBase64);
        log.info("Gemini identificó medicamento en foto: '{}'", nombreIdentificado);

        if (nombreIdentificado.equalsIgnoreCase("DESCONOCIDO") || nombreIdentificado.isBlank()) {
            guardarHistorial("(foto)", TipoBusqueda.FOTO, null, 0);
            return new BioequivalentesResponseDTO("No identificado", null, List.of());
        }
        return ejecutarBusqueda(nombreIdentificado, TipoBusqueda.FOTO);
    }

    /**
     * Ejecuta el flujo de búsqueda a partir de texto crudo extraído por OCR.
     * Limpia el texto tomando solo las primeras palabras relevantes antes de buscar.
     *
     * @param textoOcr texto completo extraído de la fotografía del medicamento
     * @return DTO con el principio activo identificado y la lista de bioequivalentes
     */
    @Transactional
    public BioequivalentesResponseDTO buscarPorOcr(String textoOcr) {
        if (textoOcr == null || textoOcr.isBlank()) {
            guardarHistorial("(ocr vacío)", TipoBusqueda.OCR, null, 0);
            return new BioequivalentesResponseDTO("No identificado", null, List.of());
        }

        // Buscar el medicamento probando combinaciones de palabras del texto OCR
        Optional<Medicamento> encontrado = buscarMedicamentoEnTextoOcr(textoOcr);

        if (encontrado.isPresent()) {
            log.info("Medicamento encontrado en BD por OCR: '{}'", encontrado.get().getNombreComercial());
            return ejecutarBusqueda(encontrado.get().getNombreComercial(), TipoBusqueda.OCR);
        }

        // No encontrado: construir respuesta placeholder con la info visible en el envase
        String nombreEstimado = extraerNombreDeTextoOcr(textoOcr);
        log.info("Medicamento '{}' no encontrado en BD. Construyendo respuesta placeholder.", nombreEstimado);
        guardarHistorial(nombreEstimado, TipoBusqueda.OCR, null, 0);
        return construirRespuestaPlaceholder(textoOcr, nombreEstimado);
    }

    private Optional<Medicamento> buscarMedicamentoEnTextoOcr(String textoOcr) {
        String textoLower = textoOcr.toLowerCase();
        String[] tokens = textoOcr.split("[\\s\\n\\r,;.:()\\[\\]]+");

        // Probar combinaciones de 3, 2 y 1 token(s) consecutivos
        for (int longitud = 3; longitud >= 1; longitud--) {
            for (int i = 0; i <= tokens.length - longitud; i++) {
                StringBuilder combo = new StringBuilder();
                for (int j = i; j < i + longitud; j++) {
                    if (!tokens[j].isBlank()) {
                        if (combo.length() > 0) combo.append(" ");
                        combo.append(tokens[j].trim());
                    }
                }
                String termino = combo.toString().trim();
                if (termino.length() < 3 || !Character.isLetter(termino.charAt(0))) continue;

                List<Medicamento> resultados = medicamentoRepository.findByNombreComercialContainingIgnoreCase(termino);
                for (Medicamento med : resultados) {
                    if (nombreComercialCoincideConOcr(med.getNombreComercial(), textoLower)) {
                        return Optional.of(med);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Verifica que todas las palabras significativas del nombre comercial están presentes
     * en el texto OCR, evitando falsos positivos por coincidencias parciales (e.g. el
     * laboratorio "Andrómaco" matcheando "Tramadol Andrómaco" en un envase de Cefadroxilo).
     */
    private boolean nombreComercialCoincideConOcr(String nombreComercial, String textoOcrLower) {
        String[] palabras = nombreComercial.split("\\s+");
        for (String palabra : palabras) {
            if (palabra.length() >= 4 && !textoOcrLower.contains(palabra.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private BioequivalentesResponseDTO construirRespuestaPlaceholder(String textoOcr, String nombreEstimado) {
        String dosis = extraerDosis(textoOcr);
        String presentacion = extraerPresentacion(textoOcr);
        String administracion = extraerAdministracion(textoOcr);
        String laboratorio = extraerLaboratorio(textoOcr);

        MedicamentoResponseDTO placeholder = new MedicamentoResponseDTO(
                0L,
                nombreEstimado,
                "N/D",
                laboratorio,
                "N/D",
                "N/D",
                dosis,
                presentacion,
                administracion,
                "N/D",
                false,
                "Medicamento no encontrado en la base de datos. Información extraída del envase.",
                null
        );

        return new BioequivalentesResponseDTO("No identificado", null, List.of(placeholder));
    }

    private String extraerDosis(String texto) {
        Pattern p = Pattern.compile(
                "\\d+[,.]?\\d*\\s*(mg/\\d+\\s*ml|mg/ml|mg|mcg|g|ui|iu)",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(texto);
        return m.find() ? m.group().trim() : "N/D";
    }

    private String extraerPresentacion(String texto) {
        Pattern p = Pattern.compile(
                "(solución\\s+oral|jarabe|comprimidos?|cápsulas?|tabletas?|crema|gel|spray|ampolla|sobre|suspensión)[^\\n]{0,30}|\\d+\\s*ml|x\\s*\\d+",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(texto);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String match = m.group().trim();
            if (!sb.toString().contains(match)) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(match);
            }
        }
        return sb.length() > 0 ? sb.toString().trim() : "N/D";
    }

    private String extraerLaboratorio(String texto) {
        String textoNorm = normalizarTexto(texto);
        return laboratorioRepository.findAll().stream()
                .filter(lab -> textoNorm.contains(normalizarTexto(lab.getNombre())))
                .map(Laboratorio::getNombre)
                .findFirst()
                .orElse("N/D");
    }

    private String normalizarTexto(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    private String extraerAdministracion(String texto) {
        String lower = texto.toLowerCase();
        if (lower.contains("oral") || lower.contains("solución") || lower.contains("jarabe")) return "Oral";
        if (lower.contains("tópic") || lower.contains("crema") || lower.contains("gel")) return "Tópica";
        if (lower.contains("inyect") || lower.contains("intravenoso") || lower.contains("intramuscular")) return "Intravenosa";
        if (lower.contains("inhal")) return "Inhalada";
        if (lower.contains("sublingual")) return "Sublingual";
        return "N/D";
    }

    /**
     * Lógica central de búsqueda compartida por los flujos manual y OCR.
     *
     * @param nombreComercial nombre del medicamento a buscar
     * @param tipoBusqueda    origen de la búsqueda (MANUAL u OCR)
     * @return DTO con bioequivalentes encontrados
     */
    private BioequivalentesResponseDTO ejecutarBusqueda(String nombreComercial, TipoBusqueda tipoBusqueda) {
        String principioActivoNombre = null;
        String categoria = null;

        // Paso 1: intentar encontrar el medicamento directamente en la BD
        Optional<Medicamento> medicamentoEnBD =
                medicamentoRepository.findByNombreComercialIgnoreCase(nombreComercial);

        if (medicamentoEnBD.isPresent()) {
            PrincipioActivo pa = medicamentoEnBD.get().getPrincipioActivo();
            principioActivoNombre = pa.getNombre();
            categoria = pa.getCategoria();
            log.info("Principio activo encontrado en BD para '{}': {}", nombreComercial, principioActivoNombre);
        } else {
            // Paso 2: consultar a Claude API si no está en BD
            log.info("Medicamento '{}' no encontrado en BD, consultando Claude API...", nombreComercial);
            String respuestaClaud = claudeApiService.identificarPrincipioActivo(nombreComercial);

            if (!respuestaClaud.isBlank() && !respuestaClaud.equalsIgnoreCase("DESCONOCIDO")) {
                principioActivoNombre = respuestaClaud;

                // Verificar si Claude identificó un principio activo que sí existe en BD
                Optional<PrincipioActivo> paEnBD =
                        principioActivoRepository.findByNombreIgnoreCase(principioActivoNombre);
                if (paEnBD.isPresent()) {
                    categoria = paEnBD.get().getCategoria();
                }
                log.info("Claude API identificó principio activo: {}", principioActivoNombre);
            }
        }

        // Paso 3: buscar todos los medicamentos con ese principio activo
        List<MedicamentoResponseDTO> medicamentos = List.of();
        if (principioActivoNombre != null && !principioActivoNombre.isBlank()) {
            medicamentos = medicamentoRepository
                    .findByPrincipioActivo_NombreIgnoreCase(principioActivoNombre)
                    .stream()
                    .map(medicamentoService::toDTO)
                    .sorted(Comparator.comparing(
                            dto -> dto.precioActual() != null
                                    ? dto.precioActual()
                                    : BigDecimal.valueOf(Long.MAX_VALUE)
                    ))
                    .collect(Collectors.toList());
        }

        // Paso 4: guardar en historial
        guardarHistorial(nombreComercial, tipoBusqueda, principioActivoNombre, medicamentos.size());

        return new BioequivalentesResponseDTO(
                principioActivoNombre != null ? principioActivoNombre : "No identificado",
                categoria,
                medicamentos
        );
    }

    /**
     * Extrae el nombre probable del medicamento de un texto OCR crudo.
     * Toma las primeras palabras (hasta 3) descartando números de dosis y símbolos.
     *
     * @param textoOcr texto completo extraído por OCR
     * @return nombre limpio del medicamento
     */
    private String extraerNombreDeTextoOcr(String textoOcr) {
        if (textoOcr == null || textoOcr.isBlank()) {
            return "";
        }
        // Dividir por espacios y tomar palabras que solo sean letras (sin dosis como "500mg")
        String[] palabras = textoOcr.trim().split("\\s+");
        StringBuilder nombre = new StringBuilder();
        int contador = 0;
        for (String palabra : palabras) {
            // Incluir solo palabras que empiecen con letra (excluye dosis, números, símbolos)
            if (palabra.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+.*") && contador < 3) {
                if (nombre.length() > 0) {
                    nombre.append(" ");
                }
                nombre.append(palabra);
                contador++;
            }
        }
        return nombre.length() > 0 ? nombre.toString() : textoOcr.split("\\s+")[0];
    }

    /**
     * Persiste un registro en el historial de búsquedas.
     * La búsqueda se guarda sin usuario asociado (anónima) ya que no hay autenticación activa.
     *
     * @param terminoBusqueda          texto buscado
     * @param tipoBusqueda             origen de la búsqueda
     * @param resultadoPrincipioActivo principio activo identificado (puede ser nulo)
     * @param resultadosEncontrados    cantidad de medicamentos encontrados
     */
    private void guardarHistorial(String terminoBusqueda, TipoBusqueda tipoBusqueda,
                                   String resultadoPrincipioActivo, int resultadosEncontrados) {
        HistorialBusqueda historial = HistorialBusqueda.builder()
                .terminoBusqueda(terminoBusqueda)
                .tipoBusqueda(tipoBusqueda)
                .resultadoPrincipioActivo(resultadoPrincipioActivo)
                .resultadosEncontrados(resultadosEncontrados)
                .build();
        historialBusquedaRepository.save(historial);
    }
}
