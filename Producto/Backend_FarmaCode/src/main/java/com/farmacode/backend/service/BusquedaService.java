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
import com.farmacode.backend.service.external.ClaudeApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final ClaudeApiService claudeApiService;
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
     * Ejecuta el flujo de búsqueda a partir de texto crudo extraído por OCR.
     * Limpia el texto tomando solo las primeras palabras relevantes antes de buscar.
     *
     * @param textoOcr texto completo extraído de la fotografía del medicamento
     * @return DTO con el principio activo identificado y la lista de bioequivalentes
     */
    @Transactional
    public BioequivalentesResponseDTO buscarPorOcr(String textoOcr) {
        // Limpiar el texto OCR: tomar solo las primeras 3 palabras (suelen contener el nombre)
        String nombreLimpio = extraerNombreDeTextoOcr(textoOcr);
        log.info("Texto OCR limpiado: '{}' → '{}'", textoOcr, nombreLimpio);
        return ejecutarBusqueda(nombreLimpio, TipoBusqueda.OCR);
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
