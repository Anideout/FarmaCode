package com.farmacode.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa a un usuario registrado de la app FarmaCode.
 * Almacena el hash BCrypt de la contraseña, nunca el texto plano.
 */
@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /** Identificador único autogenerado */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario */
    @Column(nullable = false, length = 255)
    private String nombre;

    /** Correo electrónico del usuario, único y usado para autenticación */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /** Hash BCrypt de la contraseña del usuario */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /** Indica si la cuenta del usuario está activa */
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha de registro del usuario */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha y hora del último inicio de sesión */
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    /**
     * Asigna la fecha de creación automáticamente al momento de persistir.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
