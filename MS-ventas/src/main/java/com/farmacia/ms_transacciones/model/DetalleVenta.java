package com.farmacia.ms_transacciones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas")
@Data // <--- Genera getProductoId() automáticamente
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer productoId;

    private String productoNombre; // Opcional, pero útil para historial
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    @JsonIgnore // Rompe el bucle infinito JSON
    private Venta venta;
}