package com.farmacode.backend.repository;

import com.farmacode.backend.entity.PrincipioActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link PrincipioActivo}.
 * Provee acceso a la tabla {@code principio_activo} en la base de datos.
 */
@Repository
public interface PrincipioActivoRepository extends JpaRepository<PrincipioActivo, Long> {

    /**
     * Busca un principio activo por nombre exacto, ignorando mayúsculas y minúsculas.
     *
     * @param nombre nombre del principio activo a buscar
     * @return Optional con el principio activo si existe
     */
    Optional<PrincipioActivo> findByNombreIgnoreCase(String nombre);

    /**
     * Busca principios activos cuyo nombre contenga el texto dado, sin distinción de mayúsculas.
     *
     * @param nombre fragmento del nombre a buscar
     * @return lista de principios activos que coincidan parcialmente
     */
    List<PrincipioActivo> findByNombreContainingIgnoreCase(String nombre);
}
