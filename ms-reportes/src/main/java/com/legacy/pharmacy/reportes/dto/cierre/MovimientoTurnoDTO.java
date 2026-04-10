package com.legacy.pharmacy.reportes.dto.cierre;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoTurnoDTO {
    private Long id;
    private LocalDateTime fecha;
    private String tipo;
    private BigDecimal monto;
    private String referencia;
    private String descripcion;

    // Constructores
    public MovimientoTurnoDTO() {}

    public MovimientoTurnoDTO(Long id, LocalDateTime fecha, String tipo, BigDecimal monto, String referencia, String descripcion) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.referencia = referencia;
        this.descripcion = descripcion;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public String getReferencia() { return referencia; }
    public String getDescripcion() { return descripcion; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
