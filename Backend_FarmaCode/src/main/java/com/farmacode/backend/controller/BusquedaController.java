package com.farmacode.backend.controller;

import com.farmacode.backend.dto.request.BusquedaRequestDTO;
import com.farmacode.backend.dto.request.OcrRequestDTO;
import com.farmacode.backend.dto.response.BioequivalentesResponseDTO;
import com.farmacode.backend.service.BusquedaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone los endpoints principales de búsqueda de FarmaCode.
 * Recibe el nombre comercial de un medicamento (manual u OCR) y retorna
 * la lista de bioequivalentes con precios ordenados de menor a mayor.
 */
@RestController
@RequestMapping("/api/busqueda")
@RequiredArgsConstructor
@Tag(name = "Búsqueda", description = "Endpoints para buscar medicamentos bioequivalentes por nombre comercial u OCR")
public class BusquedaController {

    private final BusquedaService busquedaService;

    /**
     * Recibe el nombre comercial de un medicamento ingresado manualmente,
     * identifica su principio activo y retorna los bioequivalentes disponibles con precios.
     *
     * @param request DTO con el nombre comercial del medicamento
     * @return lista de bioequivalentes ordenada por precio ascendente
     */
    @Operation(
            summary = "Buscar bioequivalentes por nombre comercial",
            description = "Recibe el nombre comercial de un medicamento, consulta Claude API para " +
                          "identificar el principio activo y retorna todos los bioequivalentes con precios."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
            @ApiResponse(responseCode = "400", description = "Nombre comercial vacío o inválido"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con Claude API")
    })
    @PostMapping("/nombre-comercial")
    public ResponseEntity<BioequivalentesResponseDTO> buscarPorNombreComercial(
            @Valid @RequestBody BusquedaRequestDTO request) {
        BioequivalentesResponseDTO respuesta =
                busquedaService.buscarPorNombreComercial(request.nombreComercial());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Recibe el texto crudo extraído por OCR de la fotografía de un medicamento,
     * limpia el texto, identifica el principio activo y retorna los bioequivalentes.
     *
     * @param request DTO con el texto OCR completo
     * @return lista de bioequivalentes ordenada por precio ascendente
     */
    @Operation(
            summary = "Buscar bioequivalentes desde texto OCR",
            description = "Recibe el texto extraído por reconocimiento óptico (OCR), limpia el contenido " +
                          "para extraer el nombre del medicamento y aplica el mismo flujo de búsqueda."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
            @ApiResponse(responseCode = "400", description = "Texto OCR vacío o inválido"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con Claude API")
    })
    @PostMapping("/ocr")
    public ResponseEntity<BioequivalentesResponseDTO> buscarPorOcr(
            @Valid @RequestBody OcrRequestDTO request) {
        BioequivalentesResponseDTO respuesta =
                busquedaService.buscarPorOcr(request.textoOcr());
        return ResponseEntity.ok(respuesta);
    }
}
