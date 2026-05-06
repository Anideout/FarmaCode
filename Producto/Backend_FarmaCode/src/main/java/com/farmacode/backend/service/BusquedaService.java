package com.farmacode.backend.service;

import com.farmacode.backend.dto.response.BioequivalentesResponseDTO;
import com.farmacode.backend.dto.response.MedicamentoResponseDTO;
import com.farmacode.backend.entity.HistorialBusqueda;
import com.farmacode.backend.entity.Medicamento;
import com.farmacode.backend.entity.PrincipioActivo;
import com.farmacode.backend.entity.TipoBusqueda;
import com.farmacode.backend.repository.HistorialBusquedaRepository;
import com.farmacode.backend.repository.MedicamentoRepository;
import com.farmacode.backend.repository.PrincipioActivoRepository;
import com.farmacode.backend.service.external.GeminiApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusquedaService {

    private final MedicamentoRepository medicamentoRepository;
    private final PrincipioActivoRepository principioActivoRepository;
    private final HistorialBusquedaRepository historialBusquedaRepository;
    private final GeminiApiService geminiApiService;
    private final MedicamentoService medicamentoService;

    /**
     * Búsqueda por nombre comercial ingresado manualmente.
     */
    @Transactional
    public BioequivalentesResponseDTO buscarPorNombreComercial(String nombreComercial) {
        return ejecutarBusqueda(nombreComercial.trim(), TipoBusqueda.MANUAL);
    }

    /**
     * Búsqueda por foto: Gemini Vision identifica el nombre comercial y luego
     * se aplica el flujo estándar.
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
     * Búsqueda por OCR:
     * 1. Intenta encontrar el medicamento en BD por tokens del texto.
     * 2. Si no lo encuentra, pide a Gemini que extraiga toda la información estructurada.
     * 3. Si el principio activo que Gemini identificó existe en BD, devuelve bioequivalentes.
     * 4. En caso contrario, construye un placeholder con los datos extraídos por Gemini.
     */
    @Transactional
    public BioequivalentesResponseDTO buscarPorOcr(String textoOcr, String imagenBase64) {
        boolean textoVacio = textoOcr == null || textoOcr.isBlank();

        // Sin texto ni imagen: nada que procesar
        if (textoVacio && (imagenBase64 == null || imagenBase64.isBlank())) {
            guardarHistorial("(ocr vacío)", TipoBusqueda.OCR, null, 0);
            return new BioequivalentesResponseDTO("No identificado", null, List.of());
        }

        // Paso 1: buscar en BD por tokens del texto OCR (solo si hay texto)
        if (!textoVacio) {
            Optional<Medicamento> encontrado = buscarMedicamentoEnTextoOcr(textoOcr);
            if (encontrado.isPresent()) {
                log.info("Medicamento encontrado en BD por OCR: '{}'", encontrado.get().getNombreComercial());
                return ejecutarBusqueda(encontrado.get().getNombreComercial(), TipoBusqueda.OCR);
            }
        }

        // Paso 2: decidir si usar texto o imagen según calidad del OCR
        GeminiApiService.InfoMedicamento info;
        boolean usarVision = esOcrInsuficiente(textoOcr) && imagenBase64 != null && !imagenBase64.isBlank();
        if (usarVision) {
            log.info("OCR insuficiente, usando Gemini Vision...");
            info = geminiApiService.extraerInformacionDeImagen(imagenBase64);
        } else {
            log.info("OCR suficiente, usando Gemini texto...");
            info = geminiApiService.extraerInformacionDeOcr(textoOcr);
        }

        // Paso 3: si Gemini identificó un principio activo que sí existe en BD → bioequivalentes
        if (!info.principioActivo().equals("N/D") && !info.principioActivo().equalsIgnoreCase("DESCONOCIDO")) {
            Optional<PrincipioActivo> paEnBD = principioActivoRepository.findByNombreIgnoreCase(info.principioActivo());
            if (paEnBD.isPresent()) {
                log.info("Principio activo '{}' encontrado en BD vía Gemini OCR, buscando bioequivalentes...",
                        info.principioActivo());
                List<MedicamentoResponseDTO> medicamentos = medicamentoRepository
                        .findByPrincipioActivo_NombreIgnoreCase(info.principioActivo())
                        .stream()
                        .map(medicamentoService::toDTO)
                        .sorted(Comparator.comparing(
                                dto -> dto.precioActual() != null ? dto.precioActual() : BigDecimal.valueOf(Long.MAX_VALUE)))
                        .collect(Collectors.toList());
                guardarHistorial(info.nombreComercial(), TipoBusqueda.OCR, info.principioActivo(), medicamentos.size());
                return new BioequivalentesResponseDTO(
                        info.principioActivo(), paEnBD.get().getCategoria(), medicamentos);
            }
        }

        // Paso 4: placeholder con los datos que Gemini pudo extraer del envase
        String nombreMostrar = !info.nombreComercial().equals("N/D")
                ? info.nombreComercial()
                : extraerNombreDeTextoOcr(textoOcr);
        String principioGuardar = !info.principioActivo().equals("N/D") ? info.principioActivo() : null;
        guardarHistorial(nombreMostrar, TipoBusqueda.OCR, principioGuardar, 0);
        return construirRespuestaConGemini(info, nombreMostrar);
    }

    // ─── Lógica central de búsqueda ───────────────────────────────────────────

    private BioequivalentesResponseDTO ejecutarBusqueda(String nombreComercial, TipoBusqueda tipoBusqueda) {
        String principioActivoNombre = null;
        String categoria = null;

        // Paso 1: buscar medicamento directamente en BD
        Optional<Medicamento> medicamentoEnBD =
                medicamentoRepository.findByNombreComercialIgnoreCase(nombreComercial);

        if (medicamentoEnBD.isPresent()) {
            PrincipioActivo pa = medicamentoEnBD.get().getPrincipioActivo();
            principioActivoNombre = pa.getNombre();
            categoria = pa.getCategoria();
            log.info("Principio activo encontrado en BD para '{}': {}", nombreComercial, principioActivoNombre);
        } else {
            // Paso 2: consultar a Gemini si no está en BD
            log.info("Medicamento '{}' no encontrado en BD, consultando Gemini...", nombreComercial);
            String respuestaGemini = geminiApiService.identificarPrincipioActivo(nombreComercial);

            if (!respuestaGemini.isBlank() && !respuestaGemini.equalsIgnoreCase("DESCONOCIDO")) {
                principioActivoNombre = respuestaGemini;
                Optional<PrincipioActivo> paEnBD =
                        principioActivoRepository.findByNombreIgnoreCase(principioActivoNombre);
                if (paEnBD.isPresent()) {
                    categoria = paEnBD.get().getCategoria();
                }
                log.info("Gemini identificó principio activo: {}", principioActivoNombre);
            }
        }

        // Paso 3: buscar bioequivalentes por principio activo
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

        guardarHistorial(nombreComercial, tipoBusqueda, principioActivoNombre, medicamentos.size());

        return new BioequivalentesResponseDTO(
                principioActivoNombre != null ? principioActivoNombre : "No identificado",
                categoria,
                medicamentos
        );
    }

    // ─── OCR: calidad del texto ────────────────────────────────────────────────

    /** Retorna true cuando el texto OCR tiene menos de 10 letras (demasiado ruidoso para Gemini texto). */
    private boolean esOcrInsuficiente(String textoOcr) {
        if (textoOcr == null || textoOcr.isBlank()) return true;
        long letras = textoOcr.chars().filter(Character::isLetter).count();
        return letras < 10;
    }

    // ─── OCR: búsqueda por tokens ──────────────────────────────────────────────

    private Optional<Medicamento> buscarMedicamentoEnTextoOcr(String textoOcr) {
        String textoLower = textoOcr.toLowerCase();
        String[] tokens = textoOcr.split("[\\s\\n\\r,;.:()\\[\\]]+");

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
     * Verifica que todas las palabras significativas del nombre comercial están en el texto OCR,
     * evitando falsos positivos (ej: laboratorio "Andrómaco" matcheando "Tramadol Andrómaco").
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

    // ─── Placeholder con datos de Gemini ──────────────────────────────────────

    private BioequivalentesResponseDTO construirRespuestaConGemini(GeminiApiService.InfoMedicamento info,
                                                                    String nombreMostrar) {
        MedicamentoResponseDTO placeholder = new MedicamentoResponseDTO(
                0L,
                nombreMostrar,
                info.principioActivo(),
                "N/D",
                info.laboratorio(),
                "N/D",
                info.dosis(),
                info.presentacion(),
                info.viaAdministracion(),
                "N/D",
                false,
                "Medicamento no encontrado en la base de datos. Información extraída del envase.",
                null
        );
        String principioActivo = !info.principioActivo().equals("N/D")
                ? info.principioActivo()
                : "No identificado";
        return new BioequivalentesResponseDTO(principioActivo, null, List.of(placeholder));
    }

    private String extraerNombreDeTextoOcr(String textoOcr) {
        if (textoOcr == null || textoOcr.isBlank()) return "";
        String[] palabras = textoOcr.trim().split("\\s+");
        StringBuilder nombre = new StringBuilder();
        int contador = 0;
        for (String palabra : palabras) {
            if (palabra.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+.*") && contador < 3) {
                if (nombre.length() > 0) nombre.append(" ");
                nombre.append(palabra);
                contador++;
            }
        }
        return nombre.length() > 0 ? nombre.toString() : textoOcr.split("\\s+")[0];
    }

    // ─── Historial ─────────────────────────────────────────────────────────────

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
