package com.farmacia.ms_transacciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_factura", unique = true)
    private String numeroFactura;

    // --- RELACIONES CON OTROS MS (Solo IDs) ---
    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    @Column(name = "vendedor_id", nullable = false)
    private String vendedorId; // Guardamos el ID del usuario (String/UUID)

    // --- RELACIONES INTERNAS ---
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Relación con el Turno (NUEVO HU-09)
    @Column(name = "turno_id")
    private Long turnoId;
    // Nota: Podrías hacer @ManyToOne si creas la entidad TurnoCaja,
    // por ahora lo dejo como ID para que compile rápido.

    private LocalDateTime fechaVenta;

    // --- TOTALES ---
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal impuestos;

    @Column(name = "ajuste_redondeo")
    private BigDecimal ajusteRedondeo;

    private BigDecimal total;

    // --- ESTADOS Y PAGOS ---
    @Column(name = "forma_pago")
    private String formaPago; // Usar String o crear un Enum Java

    private String estado; // 'completada', 'anulada'

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;
}
