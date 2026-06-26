package com.farmacode.backend.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequestDTO(
    @NotBlank @Size(max = 255) String nombre,
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(min = 6, max = 255) String password
) {}
