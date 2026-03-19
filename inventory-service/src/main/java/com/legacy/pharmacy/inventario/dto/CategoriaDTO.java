package com.legacy.pharmacy.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la entidad Categoria.
 * Usado tanto para recibir peticiones (crear/actualizar) como para enviar
 * respuestas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la categoría no puede estar en blanco")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    private String descripcion;

    private Boolean activa;
}
