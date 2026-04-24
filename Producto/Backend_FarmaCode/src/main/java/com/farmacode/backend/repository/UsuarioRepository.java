package com.farmacode.backend.repository;

import com.farmacode.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Usuario}.
 * Provee acceso a la tabla {@code usuario} en la base de datos.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * Usado principalmente en el proceso de autenticación.
     *
     * @param email correo electrónico del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);
}
