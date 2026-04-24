package com.farmacode.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa el precio de un medicamento en un momento dado.
 * Se mantiene historial de precios para permitir comparativas en el tiempo.
 * El precio vigente se identifica con el campo {@code vigente = true}.
 */
@Entity
@Table(name = "precio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Precio {

    /** Identificador único autogenerado */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Medicamento al que corresponde este precio */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    /** Precio en pesos chilenos (CLP) */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /** Fuente del precio (ej: "Farmacias Ahumada", "Cruz Verde", "Salcobrand") */
    @Column(length = 100)
    private String fuente;

    /** Fecha y hora en que se registró este precio */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /** Indica si este es el precio actualmente válido para el medicamento */
    @Builder.Default
    @Column(nullable = false)
    private Boolean vigente = true;

    /**
     * Asigna la fecha de registro automáticamente al momento de persistir.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }
}
