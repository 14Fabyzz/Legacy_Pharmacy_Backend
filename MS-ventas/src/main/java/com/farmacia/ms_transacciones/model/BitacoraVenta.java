package com.farmacia.ms_transacciones.model;

import com.farmacia.ms_transacciones.enums.TipoEventoAuditoria;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bitacora_ventas", indexes = {
        @Index(name = "idx_bitacora_turno", columnList = "turno_id"),
        @Index(name = "idx_bitacora_venta", columnList = "venta_id")
})
public class BitacoraVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venta_id", nullable = false)
    private Long ventaId;

    @Column(name = "turno_id", nullable = false)
    private Long turnoId;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId; // Quién

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private TipoEventoAuditoria tipoEvento;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "detalles_cambios_json", columnDefinition = "TEXT")
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

    public TipoEventoAuditoria getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoAuditoria tipoEvento) {
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
