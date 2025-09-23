package com.afb.pos_backend.authentication.dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticationRequestDTO(@NotNull(message = "El nombre de usuario es requerido.") String username,
                                       @NotNull(message = "La contraserña es requerida.") String password) {
}
