package com.legacy.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /**
     * Puede ser el nombre de usuario (login) o el correo electrónico.
     * El sistema resuelve automáticamente cuál usar.
     */
    @NotBlank(message = "El usuario o email es obligatorio")
    private String identifier;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}