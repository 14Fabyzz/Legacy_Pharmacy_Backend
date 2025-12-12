package com.farmacia.ms_transacciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos_caja")
@Data
public class TurnoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- DATOS DE APERTURA ---
    @Column(name = "usuario_id", nullable = false)
    private String usuarioId; // ID del usuario (String/UUID)

    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;

    @Column(name = "saldo_inicial", nullable = false)
    private BigDecimal saldoInicial;

    // --- DATOS DE CIERRE (Se llenan al cerrar el turno) ---
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "total_ventas_teorico")
    private BigDecimal totalVentasTeorico;

    @Column(name = "total_efectivo_teorico")
    private BigDecimal totalEfectivoTeorico;

    // --- NUEVOS CAMPOS DEL MVP (Script SQL) ---
    @Column(name = "total_tarjetas")
    private BigDecimal totalTarjetas;

    @Column(name = "total_transferencias")
    private BigDecimal totalTransferencias;

    @Column(name = "numero_ventas")
    private Integer numeroVentas;

    // --- VALIDACIÓN DE CIERRE ---
    @Column(name = "total_efectivo_real")
    private BigDecimal totalEfectivoReal; // El dinero físico contado

    private BigDecimal diferencia; // Positivo = Sobrante, Negativo = Faltante

    // --- ESTADO ---
    // Valores posibles: 'abierto', 'cerrado', 'cuadrado', 'descuadrado'
    // Se guarda como String para facilitar mapeo con PostgreSQL ENUM
    private String estado;

    @Column(name = "observaciones_cierre", columnDefinition = "TEXT")
    private String observacionesCierre;

    // --- AUDITORÍA AUTOMÁTICA ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Métodos para asignar fechas automáticamente antes de guardar
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "abierto";
        }
        // Inicializar contadores en 0 para evitar nulos en operaciones matemáticas
        if (this.totalVentasTeorico == null) this.totalVentasTeorico = BigDecimal.ZERO;
        if (this.totalEfectivoTeorico == null) this.totalEfectivoTeorico = BigDecimal.ZERO;
        if (this.totalTarjetas == null) this.totalTarjetas = BigDecimal.ZERO;
        if (this.totalTransferencias == null) this.totalTransferencias = BigDecimal.ZERO;
        if (this.numeroVentas == null) this.numeroVentas = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}