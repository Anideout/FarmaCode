package com.farmacode.backend.repository;

import com.farmacode.backend.entity.HistorialBusqueda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link HistorialBusqueda}.
 * Provee acceso a la tabla {@code historial_busqueda} en la base de datos.
 */
@Repository
public interface HistorialBusquedaRepository extends JpaRepository<HistorialBusqueda, Long> {

    /**
     * Obtiene todo el historial de búsquedas de un usuario, ordenado por fecha descendente.
     *
     * @param usuarioId ID del usuario
     * @return lista de búsquedas ordenada de más reciente a más antigua
     */
    List<HistorialBusqueda> findByUsuario_IdOrderByFechaDesc(Long usuarioId);

    /**
     * Obtiene el historial de búsquedas de un usuario de forma paginada, ordenado por fecha descendente.
     *
     * @param usuarioId ID del usuario
     * @param pageable  parámetros de paginación y ordenamiento
     * @return página de búsquedas del usuario
     */
    Page<HistorialBusqueda> findByUsuario_IdOrderByFechaDesc(Long usuarioId, Pageable pageable);
}
