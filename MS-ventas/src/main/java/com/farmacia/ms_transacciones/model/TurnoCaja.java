package com.farmacia.ms_transacciones.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos_caja")
public class TurnoCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuarioId; // ID del vendedor (String)
    private Integer sucursalId; // Por ahora lo manejamos manual o fijo

    private String estado; // 'ABIERTO', 'CERRADO'

    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;

    private BigDecimal saldoInicial;

    // Totales Calculados (Teóricos vs Reales)
    private BigDecimal totalVentasTeorico; // Lo que dice el sistema
    private BigDecimal totalEfectivoReal; // Lo que cuenta el cajero
    private BigDecimal diferencia; // Real - Teorico
    private BigDecimal totalEgresos; // Egresos (incluye devoluciones)

    private String observacionesCierre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getTotalVentasTeorico() {
        return totalVentasTeorico;
    }

    public void setTotalVentasTeorico(BigDecimal totalVentasTeorico) {
        this.totalVentasTeorico = totalVentasTeorico;
    }

    public BigDecimal getTotalEfectivoReal() {
        return totalEfectivoReal;
    }

    public void setTotalEfectivoReal(BigDecimal totalEfectivoReal) {
        this.totalEfectivoReal = totalEfectivoReal;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public String getObservacionesCierre() {
        return observacionesCierre;
    }

    public void setObservacionesCierre(String observacionesCierre) {
        this.observacionesCierre = observacionesCierre;
    }

    public BigDecimal getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(BigDecimal totalEgresos) {
        this.totalEgresos = totalEgresos;
    }
}