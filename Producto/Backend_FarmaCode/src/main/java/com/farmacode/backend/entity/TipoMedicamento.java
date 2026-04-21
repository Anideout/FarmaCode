package com.farmacode.backend.entity;

/**
 * Clasificación de un medicamento según el Instituto de Salud Pública (ISP) de Chile.
 * <ul>
 *   <li>GENERICO - Medicamento genérico sin certificación de bioequivalencia</li>
 *   <li>REFERENCIA - Medicamento de referencia (innovador/marca original)</li>
 *   <li>BIOEQUIVALENTE - Medicamento genérico con certificación de bioequivalencia del ISP</li>
 * </ul>
 */
public enum TipoMedicamento {
    GENERICO,
    REFERENCIA,
    BIOEQUIVALENTE
}
