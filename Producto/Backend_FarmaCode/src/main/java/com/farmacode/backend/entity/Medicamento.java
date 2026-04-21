package com.farmacode.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad JPA que representa un medicamento específico (producto comercial).
 * Cada medicamento pertenece a un principio activo y a un laboratorio fabricante.
 * Ejemplo: "Tapsin 500mg comprimidos x20" de laboratorio Chile.
 */
@Entity
@Table(name = "medicamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

    /** Identificador único autogenerado */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre comercial del medicamento (ej: "Tapsin", "Aspirina") */
    @Column(name = "nombre_comercial", nullable = false, length = 255)
    private String nombreComercial;

    /** Dosis del medicamento (ej: "500mg", "100mg") */
    @Column(nullable = false, length = 100)
    private String dosis;

    /** Forma de presentación (ej: "Comprimidos recubiertos x30") */
    @Column(nullable = false, length = 255)
    private String presentacion;

    /** Vía de administración (ej: "Oral", "Tópica", "Intravenosa") */
    @Column(nullable = false, length = 100)
    private String administracion;

    /** Clasificación del medicamento según el ISP Chile */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMedicamento tipo;

    /** Indica si cuenta con certificación del Instituto de Salud Pública de Chile */
    @Builder.Default
    @Column(name = "cert_isp", nullable = false)
    private Boolean certIsp = false;

    /** Descripción adicional del producto */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Principio activo al que pertenece este medicamento */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principio_activo_id", nullable = false)
    private PrincipioActivo principioActivo;

    /** Laboratorio fabricante del medicamento */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    /** Historial de precios registrados para este medicamento */
    @OneToMany(mappedBy = "medicamento", fetch = FetchType.LAZY)
    private List<Precio> precios;

    /** Fecha de creación del registro */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de la última actualización del registro */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Asigna las fechas de creación y actualización al momento de persistir por primera vez.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza la fecha de modificación cada vez que el registro es actualizado.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
