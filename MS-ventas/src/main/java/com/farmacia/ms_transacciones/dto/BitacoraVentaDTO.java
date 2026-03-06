package com.farmacia.ms_transacciones.dto;

import java.time.LocalDateTime;

public class BitacoraVentaDTO {

    private Long id;
    private Long ventaId;
    private Long turnoId;
    private String usuarioId;
    private String tipoEvento;
    private LocalDateTime fechaEvento;
    private String motivo;
    private String detallesCambiosJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Long getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDetallesCambiosJson() {
        return detallesCambiosJson;
    }

    public void setDetallesCambiosJson(String detallesCambiosJson) {
        this.detallesCambiosJson = detallesCambiosJson;
    }
}
