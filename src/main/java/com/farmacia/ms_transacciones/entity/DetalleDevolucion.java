package com.farmacia.ms_transacciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_devolucion")
@Data
public class DetalleDevolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "devolucion_id", nullable = false)
    private Devolucion devolucion;

    @ManyToOne
    @JoinColumn(name = "detalle_venta_id", nullable = false)
    private DetalleVenta detalleVenta;

    private Integer cantidad;

    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    @Column(name = "motivo_detalle")
    private String motivoDetalle;

    private String estado;

    @Column(name = "destino_producto")
    private String destinoProducto; // 'reingreso', 'destruccion', etc.
}
