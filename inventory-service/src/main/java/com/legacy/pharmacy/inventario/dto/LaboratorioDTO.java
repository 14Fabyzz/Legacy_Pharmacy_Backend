package com.legacy.pharmacy.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la entidad Laboratorio.
 * Usado tanto para recibir peticiones (crear/actualizar) como para enviar
 * respuestas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioDTO {

    private Integer id;

    @NotBlank(message = "El nombre del laboratorio no puede estar en blanco")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    private String descripcion;

    @Size(max = 100, message = "El país no puede superar los 100 caracteres")
    private String pais;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    private Boolean activo;
}
