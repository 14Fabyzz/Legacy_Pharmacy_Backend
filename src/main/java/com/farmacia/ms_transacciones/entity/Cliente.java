package com.farmacia.ms_transacciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Mapeo Explícito para evitar ambigüedad ---

    @Column(name = "tipo_identificacion")
    private String tipoIdentificacion;

    @Column(name = "numero_identificacion", unique = true)
    private String numeroIdentificacion;

    @Column(name = "nombre") // AHORA ES EXPLÍCITO
    private String nombre;

    @Column(name = "apellido") // AHORA ES EXPLÍCITO
    private String apellido;

    @Column(name = "email") // AHORA ES EXPLÍCITO
    private String email;

    @Column(name = "telefono") // AHORA ES EXPLÍCITO
    private String telefono;

    @Column(name = "tipo_cliente")
    private String tipoCliente;

    @Column(name = "estado") // AHORA ES EXPLÍCITO
    private String estado;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}