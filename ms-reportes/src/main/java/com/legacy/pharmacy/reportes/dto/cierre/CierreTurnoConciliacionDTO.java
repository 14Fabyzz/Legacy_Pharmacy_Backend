package com.legacy.pharmacy.reportes.dto.cierre;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CierreTurnoConciliacionDTO {
    private Long id;
    private String usuarioId;
    private Integer sucursalId;
    private String estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private BigDecimal saldoInicial;
    private BigDecimal totalVentasTeorico;
    private BigDecimal totalEfectivoReal;
    private BigDecimal totalEgresos;
    private BigDecimal diferencia;
    private String observacionesCierre;

    public CierreTurnoConciliacionDTO() {}

    public CierreTurnoConciliacionDTO(Long id, String usuarioId, Integer sucursalId, String estado,
                                      LocalDateTime fechaApertura, LocalDateTime fechaCierre, BigDecimal saldoInicial,
                                      BigDecimal totalVentasTeorico, BigDecimal totalEfectivoReal,
                                      BigDecimal totalEgresos, BigDecimal diferencia, String observacionesCierre) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.sucursalId = sucursalId;
        this.estado = estado;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.saldoInicial = saldoInicial;
        this.totalVentasTeorico = totalVentasTeorico;
        this.totalEfectivoReal = totalEfectivoReal;
        this.totalEgresos = totalEgresos;
        this.diferencia = diferencia;
        this.observacionesCierre = observacionesCierre;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }
    public BigDecimal getTotalVentasTeorico() { return totalVentasTeorico; }
    public void setTotalVentasTeorico(BigDecimal totalVentasTeorico) { this.totalVentasTeorico = totalVentasTeorico; }
    public BigDecimal getTotalEfectivoReal() { return totalEfectivoReal; }
    public void setTotalEfectivoReal(BigDecimal totalEfectivoReal) { this.totalEfectivoReal = totalEfectivoReal; }
    public BigDecimal getTotalEgresos() { return totalEgresos; }
    public void setTotalEgresos(BigDecimal totalEgresos) { this.totalEgresos = totalEgresos; }
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }
    public String getObservacionesCierre() { return observacionesCierre; }
    public void setObservacionesCierre(String observacionesCierre) { this.observacionesCierre = observacionesCierre; }
}
