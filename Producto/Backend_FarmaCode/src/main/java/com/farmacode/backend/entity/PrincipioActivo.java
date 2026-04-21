package com.farmacode.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad JPA que representa un principio activo farmacológico.
 * Es la entidad central que agrupa todos los medicamentos bioequivalentes.
 * Por ejemplo: "Paracetamol", "Ibuprofeno", "Atorvastatina".
 */
@Entity
@Table(name = "principio_activo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrincipioActivo {

    /** Identificador único autogenerado */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del principio activo, único en la base de datos */
    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    /** Descripción farmacológica general del principio activo */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Categoría terapéutica (ej: "Analgésico", "Antiinflamatorio AINE") */
    @Column(nullable = false, length = 100)
    private String categoria;

    /** Fecha de creación del registro, asignada automáticamente antes de persistir */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Lista de medicamentos que contienen este principio activo */
    @OneToMany(mappedBy = "principioActivo", fetch = FetchType.LAZY)
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
