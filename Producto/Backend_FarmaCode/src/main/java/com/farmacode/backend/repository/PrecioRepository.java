package com.farmacode.backend.repository;

import com.farmacode.backend.entity.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Precio}.
 * Provee acceso a la tabla {@code precio} en la base de datos.
 */
@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {

    /**
     * Obtiene el precio más reciente de un medicamento filtrado por estado de vigencia.
     * Útil para obtener el precio vigente actual de un medicamento.
     *
     * @param medicamentoId ID del medicamento
     * @param vigente       true para obtener el precio vigente, false para el último no vigente
     * @return Optional con el precio más reciente que coincida
     */
    Optional<Precio> findTopByMedicamento_IdAndVigenteOrderByFechaRegistroDesc(
            Long medicamentoId, Boolean vigente);

    /**
     * Obtiene todos los precios de un medicamento ordenados por fecha descendente (más reciente primero).
     * Permite consultar el historial completo de precios.
     *
     * @param medicamentoId ID del medicamento
     * @return lista de precios ordenada de más reciente a más antiguo
     */
    List<Precio> findByMedicamento_IdOrderByFechaRegistroDesc(Long medicamentoId);
}
