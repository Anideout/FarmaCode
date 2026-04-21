package com.farmacode.backend.service;

import com.farmacode.backend.dto.response.HistorialBusquedaDTO;
import com.farmacode.backend.entity.HistorialBusqueda;
import com.farmacode.backend.exception.ResourceNotFoundException;
import com.farmacode.backend.repository.HistorialBusquedaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para consulta y eliminación del historial de búsquedas.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistorialService {

    private final HistorialBusquedaRepository historialBusquedaRepository;

    /**
     * Retorna el historial de búsquedas de un usuario ordenado por fecha descendente.
     *
     * @param usuarioId ID del usuario cuyo historial se quiere consultar
     * @return lista de búsquedas en formato DTO
     */
    public List<HistorialBusquedaDTO> getHistorialByUsuario(Long usuarioId) {
        return historialBusquedaRepository.findByUsuario_IdOrderByFechaDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un registro del historial de búsquedas por su ID.
     *
     * @param id ID del registro de historial a eliminar
     * @throws ResourceNotFoundException si no existe un registro con ese ID
     */
    @Transactional
    public void deleteHistorial(Long id) {
        HistorialBusqueda historial = historialBusquedaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registro de historial no encontrado con ID: " + id));
        historialBusquedaRepository.delete(historial);
    }

    /**
     * Convierte una entidad {@link HistorialBusqueda} a su DTO de respuesta.
     *
     * @param historial entidad a convertir
     * @return DTO con los datos del registro de historial
     */
    private HistorialBusquedaDTO toDTO(HistorialBusqueda historial) {
        return new HistorialBusquedaDTO(
                historial.getId(),
                historial.getTerminoBusqueda(),
                historial.getTipoBusqueda() != null
                        ? historial.getTipoBusqueda().name() : null,
                historial.getResultadoPrincipioActivo(),
                historial.getResultadosEncontrados(),
                historial.getFecha()
        );
    }
}
