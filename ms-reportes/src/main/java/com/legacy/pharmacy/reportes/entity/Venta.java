package com.legacy.pharmacy.reportes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que mapea la tabla 'ventas' de la BD de MS-ventas (solo lectura).
 * No se usa ddl-auto para no alterar el esquema.
 */
@Entity
@Table(name = "ventas")
@Getter
@Setter
@NoArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_factura")
    private String numeroFactura;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @Column(name = "vendedor_id")
    private String vendedorId;

    @Column(name = "vendedor_nombre")
    private String vendedorNombre;

    @Column(name = "sucursal_id")
    private Integer sucursalId;

    @Column(name = "total", precision = 38, scale = 2)
    private BigDecimal total;

    @Column(name = "total_iva", precision = 38, scale = 2)
    private BigDecimal totalIva;

    @Column(name = "monto_recibido")
    private BigDecimal montoRecibido;

    @Column(name = "cambio")
    private BigDecimal cambio;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "referencia_pago")
    private String referenciaPago;

    @Column(name = "estado")
    private String estado;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "turno_id")
    private Long turnoId;
}
