package com.farmacode.backend.controller;

import com.farmacode.backend.dto.response.HistorialBusquedaDTO;
import com.farmacode.backend.service.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para consulta y gestión del historial de búsquedas.
 * Mientras no haya autenticación implementada, opera con un userId fijo (1L).
 */
@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Tag(name = "Historial", description = "Endpoints para consultar y eliminar el historial de búsquedas del usuario")
public class HistorialController {

    /**
     * ID de usuario fijo usado mientras no existe autenticación activa.
     * Cuando se implemente Spring Security + JWT, se reemplazará por el usuario del contexto.
     */
    private static final Long HARDCODED_USER_ID = 1L;

    private final HistorialService historialService;

    /**
     * Retorna el historial de búsquedas del usuario actual (userId=1 por ahora).
     *
     * @return lista de búsquedas ordenada por fecha descendente
     */
    @Operation(
            summary = "Obtener historial de búsquedas",
            description = "Retorna el historial de búsquedas del usuario autenticado. " +
                          "Por el momento opera con userId=1 (sin autenticación activa)."
    )
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<HistorialBusquedaDTO>> getHistorial() {
        return ResponseEntity.ok(historialService.getHistorialByUsuario(HARDCODED_USER_ID));
    }

    /**
     * Elimina un registro del historial por su ID.
     *
     * @param id ID del registro de historial a eliminar
     * @return 204 No Content si se eliminó exitosamente
     */
    @Operation(
            summary = "Eliminar registro del historial",
            description = "Elimina permanentemente un registro del historial de búsquedas dado su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistorial(
            @Parameter(description = "ID del registro de historial a eliminar") @PathVariable Long id) {
        historialService.deleteHistorial(id);
        return ResponseEntity.noContent().build();
    }
}
