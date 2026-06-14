package com.farmacode.backend.dto.request;

import java.util.List;

public record ChatRequestDTO(
        String mensaje,
        List<TurnoChat> historial
) {
    public record TurnoChat(String rol, String contenido) {}
}
