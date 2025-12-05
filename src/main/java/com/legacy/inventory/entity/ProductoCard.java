package com.legacy.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "v_stock_productos")
@Data
public class ProductoCard {

    @Id
    @Column(name = "producto_id")
    private Integer id;

    @Column(name = "codigo_interno")
    private String codigoInterno;

    // --- NUEVO CAMPO AGREGADO ---
    @Column(name = "codigo_barras")
    private String codigoBarras;
    // ----------------------------

    @Column(name = "nombre_comercial")
    private String nombre;

    private String laboratorio;
    private String categoria;

    @Column(name = "principio_activo")
    private String principioActivo;

    @Column(name = "stock_total")
    private Integer stockTotal;

    @Column(name = "precio_venta_base")
    private BigDecimal precio;

    @Column(name = "nivel_stock")
    private String nivelStock;
}