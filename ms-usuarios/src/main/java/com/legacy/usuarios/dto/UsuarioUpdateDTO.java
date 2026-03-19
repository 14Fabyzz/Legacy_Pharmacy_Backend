package com.legacy.usuarios.dto;

import com.legacy.usuarios.entity.Usuario.EstadoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDTO {

    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombreCompleto;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", message = "La contraseña debe contener al menos: 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial")
    private String password;

    // Si se envía email, debe tener formato válido y no exceder 100 caracteres
    @Email(message = "El formato del email no es válido")
    @Size(max = 80, message = "El email no puede exceder 80 caracteres")
    private String email;

    // Si se envía teléfono, debe cumplir el formato internacional básico
    @Size(max = 13, message = "El teléfono no puede exceder 13 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-().]{7,13}$", message = "El teléfono solo puede contener números, espacios, +, -, (, )")
    private String telefono;

    private Long rolId;

    private Long sucursalId;

    private EstadoUsuario estado;
}