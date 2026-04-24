package com.farmacode.backend.repository;

import com.farmacode.backend.entity.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Laboratorio}.
 * Provee acceso a la tabla {@code laboratorio} en la base de datos.
 */
@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    /**
     * Busca un laboratorio por nombre exacto, ignorando mayúsculas y minúsculas.
     *
     * @param nombre nombre del laboratorio a buscar
     * @return Optional con el laboratorio si existe
     */
    Optional<Laboratorio> findByNombreIgnoreCase(String nombre);
}
