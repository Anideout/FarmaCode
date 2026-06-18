package com.farmacode.backend.controller;

import com.farmacode.backend.dto.request.BusquedaRequestDTO;
import com.farmacode.backend.dto.request.FotoRequestDTO;
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

@RestController
@RequestMapping("/api/busqueda")
@RequiredArgsConstructor
@Tag(name = "Búsqueda", description = "Endpoints para buscar medicamentos bioequivalentes por nombre comercial u OCR")
public class BusquedaController {

    private final BusquedaService busquedaService;

    @Operation(summary = "Buscar bioequivalentes por nombre comercial")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
            @ApiResponse(responseCode = "400", description = "Nombre comercial vacío o inválido"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con Gemini API")
    })
    @PostMapping("/nombre-comercial")
    public ResponseEntity<BioequivalentesResponseDTO> buscarPorNombreComercial(
            @Valid @RequestBody BusquedaRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(busquedaService.buscarPorNombreComercial(request.nombre(), userId));
    }

    @Operation(summary = "Buscar bioequivalentes desde texto OCR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
            @ApiResponse(responseCode = "400", description = "Texto OCR vacío o inválido"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con Gemini API")
    })
    @PostMapping("/ocr")
    public ResponseEntity<BioequivalentesResponseDTO> buscarPorOcr(
            @RequestBody OcrRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(busquedaService.buscarPorOcr(request.textoOcr(), request.imagenBase64(), userId));
    }

    @Operation(summary = "Buscar bioequivalentes desde fotografía")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
            @ApiResponse(responseCode = "400", description = "Imagen vacía o inválida"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con Gemini API")
    })
    @PostMapping("/foto")
    public ResponseEntity<BioequivalentesResponseDTO> buscarPorFoto(
            @Valid @RequestBody FotoRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(busquedaService.buscarPorFoto(request.imagenBase64(), userId));
    }
}
