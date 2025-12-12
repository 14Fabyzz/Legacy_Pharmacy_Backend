package com.farmacia.ms_transacciones.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas")
@Data
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    @JsonIgnore //Para no entrar en un bucle infinot al imprimir detalleVenta
    private Venta venta;

    // --- SNAPSHOT DEL PRODUCTO (Datos congelados) ---
    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Column(name = "producto_nombre", nullable = false)
    private String productoNombre;

    @Column(name = "producto_codigo")
    private String productoCodigo;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;

    @Column(name = "lote_vendido")
    private String loteVendido;
}