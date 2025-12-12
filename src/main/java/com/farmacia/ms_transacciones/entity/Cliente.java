package com.farmacia.ms_transacciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_identificacion")
    private String tipoIdentificacion;

    @Column(name = "numero_identificacion", unique = true)
    private String numeroIdentificacion;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Campos nuevos del MVP
    @Column(name = "tipo_cliente")
    private String tipoCliente;
}