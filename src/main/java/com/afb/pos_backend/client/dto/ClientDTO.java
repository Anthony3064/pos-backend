package com.afb.pos_backend.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientDTO {

    private String id;
    @NotBlank(message = "El nombre del cliente es obligatorio.")
    private String completeName;

    @Email(message = "Debe ser un correo electrónico válido.")
    private String email;

    private String phoneNumber;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthdate;

    private Boolean active;
}
