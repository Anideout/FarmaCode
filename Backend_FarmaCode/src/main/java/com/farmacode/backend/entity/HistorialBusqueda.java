package com.farmacode.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que registra cada búsqueda realizada en la app FarmaCode.
 * La búsqueda puede ser anónima (usuario_id nulo) o asociada a un usuario autenticado.
 */
@Entity
@Table(name = "historial_busqueda")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialBusqueda {

    /** Identificador único autogenerado */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que realizó la búsqueda. Puede ser nulo si la búsqueda fue anónima.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    /** Texto ingresado manualmente o extraído por OCR */
    @Column(name = "termino_busqueda", nullable = false, length = 255)
    private String terminoBusqueda;

    /** Origen de la búsqueda: ingresada a mano (MANUAL) o desde cámara (OCR) */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_busqueda", nullable = false)
    private TipoBusqueda tipoBusqueda;

    /** Principio activo identificado por Claude API para esta búsqueda */
    @Column(name = "resultado_principio_activo", length = 255)
    private String resultadoPrincipioActivo;

    /** Cantidad de medicamentos bioequivalentes encontrados */
    @Builder.Default
    @Column(name = "resultados_encontrados", nullable = false)
    private Integer resultadosEncontrados = 0;

    /** Fecha y hora en que se realizó la búsqueda */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    /**
     * Asigna la fecha automáticamente al momento de persistir.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}
