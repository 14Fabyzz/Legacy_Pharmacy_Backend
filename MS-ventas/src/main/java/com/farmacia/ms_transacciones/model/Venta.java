package com.farmacia.ms_transacciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFactura;
    private LocalDateTime fechaVenta;

    // --- DATOS DEL VENDEDOR Y SUCURSAL (Para el Voucher) ---
    private String vendedorId; // ID del usuario (Token)
    private String vendedorNombre; // Nombre (Token) -> NUEVO
    private Integer sucursalId; // Sucursal del turno -> NUEVO
    // -------------------------------------------------------

    private BigDecimal total;
    private BigDecimal totalIva; // <-- IVA total de la venta

    private BigDecimal montoRecibido;
    private BigDecimal cambio;

    // --- DATOS DE PAGO ---
    @Enumerated(EnumType.STRING)
    private com.farmacia.ms_transacciones.enums.MetodoPago metodoPago; // 'EFECTIVO', 'TARJETA', 'TRANSFERENCIA'
    private String referenciaPago; // Ej: "Bancolombia a la cuenta 987..." -> NUEVO
    // ---------------------

    private String estado;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "turno_id")
    @JsonIgnoreProperties("ventas")
    private TurnoCaja turno;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("venta")
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(String vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getVendedorNombre() {
        return vendedorNombre;
    }

    public void setVendedorNombre(String vendedorNombre) {
        this.vendedorNombre = vendedorNombre;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(BigDecimal totalIva) {
        this.totalIva = totalIva;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    public com.farmacia.ms_transacciones.enums.MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(com.farmacia.ms_transacciones.enums.MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TurnoCaja getTurno() {
        return turno;
    }

    public void setTurno(TurnoCaja turno) {
        this.turno = turno;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}