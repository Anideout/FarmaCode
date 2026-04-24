package com.farmacode.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad JPA que representa un laboratorio farmacéutico fabricante.
 * Ejemplos de laboratorios chilenos: Recalcine, Mintlab, Saval, Bagó, Andrómaco.
 */
@Entity
@Table(name = "laboratorio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Laboratorio {

    /** Identificador único autogenerado */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del laboratorio, único en la base de datos */
    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    /** País de origen del laboratorio (ej: "Chile", "Argentina") */
    @Column(nullable = false, length = 100)
    private String pais;

    /** Fecha de creación del registro */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Lista de medicamentos fabricados por este laboratorio */
    @OneToMany(mappedBy = "laboratorio", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Medicamento> medicamentos;

    /**
     * Asigna la fecha de creación automáticamente al momento de persistir.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
