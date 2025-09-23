package com.afb.pos_backend.authentication.dto;

public record AuthenticationResponseDTO(String jwt, String refreshToken) {
}
