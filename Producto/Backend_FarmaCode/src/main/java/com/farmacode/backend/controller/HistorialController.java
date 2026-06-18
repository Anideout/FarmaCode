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

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Tag(name = "Historial", description = "Endpoints para consultar y eliminar el historial de búsquedas del usuario")
public class HistorialController {

    private final HistorialService historialService;

    @Operation(summary = "Obtener historial de búsquedas")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<HistorialBusquedaDTO>> getHistorial(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(historialService.getHistorialByUsuario(userId));
    }

    @Operation(summary = "Eliminar registro del historial")
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
