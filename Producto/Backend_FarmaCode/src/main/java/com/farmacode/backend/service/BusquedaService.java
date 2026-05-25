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
import java.util.ArrayList;
import java.util.Arrays;
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

        if (nombreIdentificado.equalsIgnoreCase("NO_ES_MEDICAMENTO")) {
            guardarHistorial("(foto no medicamento)", TipoBusqueda.FOTO, null, 0);
            return new BioequivalentesResponseDTO("NO_ES_MEDICAMENTO", null, List.of());
        }
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

        // Fallback: si Gemini no identificó principio activo, pedirlo explícitamente por nombre
        if (info.principioActivo().equals("N/D") || info.principioActivo().equalsIgnoreCase("DESCONOCIDO")) {
            String nombreParaBuscar = !info.nombreComercial().equals("N/D")
                    ? info.nombreComercial()
                    : extraerNombreDeTextoOcr(textoOcr);
            if (!nombreParaBuscar.isBlank()) {
                String paIdentificado = geminiApiService.identificarPrincipioActivo(nombreParaBuscar);
                if (!paIdentificado.isBlank() && !paIdentificado.equalsIgnoreCase("DESCONOCIDO")) {
                    info = new GeminiApiService.InfoMedicamento(
                            info.nombreComercial(), paIdentificado, info.dosis(),
                            info.presentacion(), info.laboratorio(), info.paisOrigen(),
                            info.viaAdministracion(), info.descripcionGeneral());
                }
            }
        }

        // Imagen completamente ilegible: Gemini no pudo extraer ningún dato útil
        if (info.nombreComercial().equals("N/D") && info.principioActivo().equals("N/D")) {
            guardarHistorial("(imagen ilegible)", TipoBusqueda.OCR, null, 0);
            return new BioequivalentesResponseDTO("IMAGEN_ILEGIBLE", null, List.of());
        }

        // El objeto escaneado no es un medicamento
        if ("NO_ES_MEDICAMENTO".equals(info.nombreComercial())) {
            guardarHistorial("(no es medicamento)", TipoBusqueda.OCR, null, 0);
            return new BioequivalentesResponseDTO("NO_ES_MEDICAMENTO", null, List.of());
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

        // Buscar bioequivalentes en BD por la primera palabra del principio activo (búsqueda parcial)
        List<MedicamentoResponseDTO> bioequivalentesBD = new ArrayList<>();
        if (principioGuardar != null) {
            String primPalabra = principioGuardar.split("\\s+")[0];
            if (primPalabra.length() >= 5) {
                medicamentoRepository
                        .findByPrincipioActivo_NombreContainingIgnoreCase(primPalabra)
                        .stream()
                        .map(medicamentoService::toDTO)
                        .sorted(Comparator.comparing(dto ->
                                dto.precioActual() != null ? dto.precioActual() : BigDecimal.valueOf(Long.MAX_VALUE)))
                        .forEach(bioequivalentesBD::add);
            }
        }

        guardarHistorial(nombreMostrar, TipoBusqueda.OCR, principioGuardar, bioequivalentesBD.size());
        return construirRespuestaConGemini(info, nombreMostrar, bioequivalentesBD);
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

    private boolean esOcrInsuficiente(String textoOcr) {
        if (textoOcr == null || textoOcr.isBlank()) return true;
        long letras = textoOcr.chars().filter(Character::isLetter).count();
        if (letras < 15) return true;
        // Texto demasiado ruidoso: menos del 40% del contenido son letras
        double ratioLetras = (double) letras / textoOcr.trim().length();
        if (ratioLetras < 0.40) return true;
        // Necesita al menos 2 palabras reconocibles (≥4 letras)
        long palabrasValidas = Arrays.stream(textoOcr.split("\\s+"))
                .filter(w -> w.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]{4,}"))
                .count();
        return palabrasValidas < 2;
    }

    // ─── OCR: búsqueda por tokens ──────────────────────────────────────────────

    private Optional<Medicamento> buscarMedicamentoEnTextoOcr(String textoOcr) {
        String textoLower = textoOcr.toLowerCase();
        // Solo tokens que sean palabras reales (≥4 letras, empieza con letra)
        String[] tokens = Arrays.stream(textoOcr.split("[\\s\\n\\r,;.:()\\[\\]/\\\\]+"))
                .map(String::trim)
                .filter(t -> t.length() >= 4 && Character.isLetter(t.charAt(0)))
                .toArray(String[]::new);

        // Primero intenta combos de 3 y 2 tokens; los de 1 token solo si son ≥5 chars
        for (int longitud = 3; longitud >= 1; longitud--) {
            for (int i = 0; i <= tokens.length - longitud; i++) {
                StringBuilder combo = new StringBuilder();
                for (int j = i; j < i + longitud; j++) {
                    if (combo.length() > 0) combo.append(" ");
                    combo.append(tokens[j]);
                }
                String termino = combo.toString().trim();
                // Para búsquedas de un solo token exigir al menos 5 caracteres
                if (longitud == 1 && termino.length() < 5) continue;

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
     * Verifica que todas las palabras significativas (≥5 chars) del nombre comercial
     * están presentes en el texto OCR, y que la primera palabra del nombre también
     * está en el OCR (evita matchear solo por laboratorio o sufijos genéricos).
     */
    private boolean nombreComercialCoincideConOcr(String nombreComercial, String textoOcrLower) {
        String[] palabras = nombreComercial.split("\\s+");
        // La primera palabra del nombre comercial SIEMPRE debe aparecer en el OCR
        if (palabras.length > 0 && !textoOcrLower.contains(palabras[0].toLowerCase())) {
            return false;
        }
        // Todas las palabras significativas deben estar presentes
        for (String palabra : palabras) {
            if (palabra.length() >= 5 && !textoOcrLower.contains(palabra.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    // ─── Placeholder con datos de Gemini ──────────────────────────────────────

    private BioequivalentesResponseDTO construirRespuestaConGemini(GeminiApiService.InfoMedicamento info,
                                                                    String nombreMostrar,
                                                                    List<MedicamentoResponseDTO> bioequivalentes) {
        String laboratorio = (info.laboratorio() == null || info.laboratorio().equals("N/D"))
                ? "No detectado"
                : info.laboratorio();
        String paisOrigen = (info.paisOrigen() == null || info.paisOrigen().equals("N/D"))
                ? "No detectado"
                : info.paisOrigen();
        String descripcion = (info.descripcionGeneral() == null || info.descripcionGeneral().equals("N/D"))
                ? "Información no disponible para este medicamento."
                : info.descripcionGeneral();

        // Si el nombre comercial empieza con el laboratorio (ej: "HETERO Levocetirizina"), quitarlo
        String nombreFinal = nombreMostrar;
        if (!laboratorio.equals("No detectado") &&
                nombreFinal.toUpperCase().startsWith(laboratorio.toUpperCase())) {
            nombreFinal = nombreFinal.substring(laboratorio.length()).trim();
        }

        // Safety net: si Gemini asignó como laboratorio solo el sufijo de un fabricante
        // (ej: nombreComercial="Ascend", laboratorio="Laboratories"),
        // reconstruir el nombre real del lab y usar el principio activo como nombre del medicamento
        if (!info.principioActivo().equals("N/D") && !laboratorio.equals("No detectado")) {
            List<String> sufijosLab = List.of("laboratories", "labs", "pharma", "pharmaceuticals",
                    "healthcare", "medical", "biotech", "biosciences", "generics");
            if (sufijosLab.contains(laboratorio.toLowerCase())) {
                laboratorio = nombreFinal + " " + laboratorio;
                nombreFinal = info.principioActivo();
            }
        }

        // Normalizar capitalización para los campos que Gemini suele devolver en MAYÚSCULAS
        String nombreNorm      = normalizarCapitalizacion(nombreFinal);
        String laboratorioNorm = normalizarCapitalizacion(laboratorio);
        String principioNorm   = normalizarCapitalizacion(info.principioActivo());

        MedicamentoResponseDTO placeholder = new MedicamentoResponseDTO(
                0L,
                nombreNorm,
                principioNorm,
                "N/D",
                laboratorioNorm,
                paisOrigen,
                info.dosis(),
                info.presentacion(),
                info.viaAdministracion(),
                "Escaneado",
                false,
                descripcion,
                null
        );
        String principioActivo = !info.principioActivo().equals("N/D")
                ? principioNorm
                : "No identificado";
        List<MedicamentoResponseDTO> medicamentos = new ArrayList<>();
        medicamentos.add(placeholder);
        medicamentos.addAll(bioequivalentes);
        return new BioequivalentesResponseDTO(principioActivo, null, medicamentos);
    }

    private String normalizarCapitalizacion(String texto) {
        if (texto == null || texto.isBlank() || texto.equals("N/D")) return texto;
        String letras = texto.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]", "");
        if (letras.isEmpty()) return texto;
        long mayus = letras.chars().filter(Character::isUpperCase).count();
        if ((double) mayus / letras.length() < 0.75) return texto;
        return Arrays.stream(texto.split(" "))
                .map(w -> w.isEmpty() ? w :
                        Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
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
