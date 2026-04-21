package com.farmacode.backend.entity;

/**
 * Origen de la búsqueda realizada por el usuario en la app FarmaCode.
 * <ul>
 *   <li>MANUAL - El usuario escribió el nombre del medicamento a mano</li>
 *   <li>OCR - El nombre fue extraído de una fotografía mediante reconocimiento óptico</li>
 * </ul>
 */
public enum TipoBusqueda {
    MANUAL,
    OCR
}
