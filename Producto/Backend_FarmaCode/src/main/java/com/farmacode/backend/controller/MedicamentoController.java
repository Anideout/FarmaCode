package com.farmacode.backend.controller;

import com.farmacode.backend.dto.response.MedicamentoResponseDTO;
import com.farmacode.backend.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para consulta y búsqueda de medicamentos.
 * Expone endpoints para listar, buscar por nombre y filtrar por principio activo.
 */
@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
@Tag(name = "Medicamentos", description = "Endpoints para listar y buscar medicamentos en la base de datos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    /**
     * Retorna todos los medicamentos de forma paginada.
     * Por defecto retorna 20 elementos por página ordenados por ID.
     *
     * @param pageable parámetros de paginación (page, size, sort)
     * @return página de medicamentos
     */
    @Operation(
            summary = "Listar todos los medicamentos",
            description = "Retorna todos los medicamentos registrados de forma paginada."
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<Page<MedicamentoResponseDTO>> getAllMedicamentos(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(medicamentoService.getAllMedicamentos(pageable));
    }

    /**
     * Retorna el detalle de un medicamento por su ID.
     *
     * @param id identificador del medicamento
     * @return datos del medicamento
     */
    @Operation(
            summary = "Obtener medicamento por ID",
            description = "Retorna el detalle completo de un medicamento dado su identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medicamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Medicamento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> getMedicamentoById(
            @Parameter(description = "ID del medicamento") @PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.getMedicamentoById(id));
    }

    /**
     * Busca medicamentos cuyo nombre comercial contenga el texto dado.
     *
     * @param nombre fragmento del nombre comercial a buscar
     * @return lista de medicamentos que coincidan
     */
    @Operation(
            summary = "Buscar medicamentos por nombre comercial",
            description = "Busca medicamentos cuyo nombre comercial contenga el texto proporcionado (búsqueda parcial)."
    )
    @ApiResponse(responseCode = "200", description = "Búsqueda exitosa (puede retornar lista vacía)")
    @GetMapping("/buscar")
    public ResponseEntity<List<MedicamentoResponseDTO>> buscarPorNombre(
            @Parameter(description = "Fragmento del nombre comercial a buscar")
            @RequestParam String nombre) {
        return ResponseEntity.ok(medicamentoService.searchByNombre(nombre));
    }

    /**
     * Retorna todos los medicamentos con un principio activo dado, ordenados por precio ascendente.
     *
     * @param nombre nombre del principio activo
     * @return lista de medicamentos ordenada por precio
     */
    @Operation(
            summary = "Listar medicamentos por principio activo",
            description = "Retorna todos los medicamentos que comparten el principio activo indicado, " +
                          "ordenados de menor a mayor precio vigente."
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente (puede retornar lista vacía)")
    @GetMapping("/principio-activo/{nombre}")
    public ResponseEntity<List<MedicamentoResponseDTO>> getByPrincipioActivo(
            @Parameter(description = "Nombre del principio activo") @PathVariable String nombre) {
        return ResponseEntity.ok(medicamentoService.getByPrincipioActivo(nombre));
    }
}
