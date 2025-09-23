package com.afb.pos_backend.user.dto;

import com.afb.pos_backend.common.dto.RoleUser;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;

    @NotNull(message = "El nombre de usuario es obligatorio.")
    private String username;

    @NotNull(message = "El nombre completo es obligatorio.")
    private String completeName;

    @NotNull(message = "La contraseña es obligatoria.")
    private String password;

    private RoleUser role;
}
