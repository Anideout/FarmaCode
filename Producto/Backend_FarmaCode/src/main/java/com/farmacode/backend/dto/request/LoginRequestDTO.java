package com.farmacode.backend.dto.request;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(
    @NotBlank @Email String email,
    @NotBlank String password
) {}
