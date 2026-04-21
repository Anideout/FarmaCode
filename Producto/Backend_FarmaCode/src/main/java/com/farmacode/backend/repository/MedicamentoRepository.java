package com.farmacode.backend.repository;

import com.farmacode.backend.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Medicamento}.
 * Provee acceso a la tabla {@code medicamento} en la base de datos.
 */
@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    /**
     * Busca medicamentos cuyo nombre comercial contenga el texto dado, sin distinción de mayúsculas.
     *
     * @param nombre fragmento del nombre comercial a buscar
     * @return lista de medicamentos que coincidan parcialmente
     */
    List<Medicamento> findByNombreComercialContainingIgnoreCase(String nombre);

    /**
     * Busca medicamentos por el ID de su principio activo.
     *
     * @param principioActivoId ID del principio activo
     * @return lista de medicamentos con ese principio activo
     */
    List<Medicamento> findByPrincipioActivo_Id(Long principioActivoId);

    /**
     * Busca medicamentos por el nombre exacto de su principio activo, ignorando mayúsculas.
     *
     * @param nombre nombre del principio activo
     * @return lista de medicamentos con ese principio activo
     */
    List<Medicamento> findByPrincipioActivo_NombreIgnoreCase(String nombre);

    /**
     * Busca un medicamento por su nombre comercial exacto, ignorando mayúsculas y minúsculas.
     *
     * @param nombre nombre comercial exacto a buscar
     * @return Optional con el medicamento si existe
     */
    Optional<Medicamento> findByNombreComercialIgnoreCase(String nombre);
}
