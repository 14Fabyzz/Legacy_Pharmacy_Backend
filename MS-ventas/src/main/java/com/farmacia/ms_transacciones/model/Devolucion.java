package com.farmacia.ms_transacciones.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private TurnoCaja turno;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "motivo_general", length = 500, nullable = false)
    private String motivoGeneral;

    @Column(name = "total_devuelto", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalDevuelto;

    @Column(name = "estado", length = 50, nullable = false)
    private String estado; // ej. COMPLETA, RECHAZADA

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public TurnoCaja getTurno() {
        return turno;
    }

    public void setTurno(TurnoCaja turno) {
        this.turno = turno;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivoGeneral() {
        return motivoGeneral;
    }

    public void setMotivoGeneral(String motivoGeneral) {
        this.motivoGeneral = motivoGeneral;
    }

    public BigDecimal getTotalDevuelto() {
        return totalDevuelto;
    }

    public void setTotalDevuelto(BigDecimal totalDevuelto) {
        this.totalDevuelto = totalDevuelto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
