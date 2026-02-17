package com.legacy.pharmacy.inventario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.time.LocalDate;
import org.hibernate.annotations.Formula;
import jakarta.persistence.Transient;

@Data
@Entity
@Immutable
@Table(name = "v_stock_productos")
public class ProductoCard {

    @Id
    @Column(name = "producto_id")
    private Long id;

    // ... tus otros campos (codigo, nombre, etc) ...
    @Column(name = "codigo_interno")
    private String codigoInterno;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    private String concentracion;
    private String presentacion;

    @Column(name = "precio_venta_base")
    private Double precioVentaBase;

    @Column(name = "precio_venta_total")
    private Double precioVentaTotal; // NUEVO: PVP final con IVA

    @Column(name = "precio_venta_unidad")
    private Double precioVentaUnidad;

    @Column(name = "precio_venta_blister")
    private Double precioVentaBlister; // NUEVO: Precio por blister

    @Column(name = "iva_porcentaje")
    private Integer ivaPorcentaje; // NUEVO: Porcentaje de IVA

    // ✅ STOCK DINÁMICO (Calculado por Lotes)
    @Formula("(SELECT COALESCE(SUM(l.cantidad_actual), 0) FROM lotes l WHERE l.producto_id = producto_id AND l.cantidad_actual > 0)")
    private Integer stockTotal;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    // ✅ CAMPOS FRACCIONAMIENTO
    @Column(name = "es_fraccionable")
    private Boolean esFraccionable;

    @Column(name = "unidades_por_caja")
    private Integer unidadesPorCaja;

    // ✅ CAMPOS DE SEGURIDAD FARMACÉUTICA
    @Column(name = "refrigerado")
    private Boolean refrigerado; // NUEVO: Requiere cadena de frío

    @Column(name = "es_controlado")
    private Boolean esControlado; // NUEVO: Requiere receta controlada

    // ✅ EL NUEVO CAMPO:
    @Column(name = "proximo_vencimiento")
    private LocalDate proximoVencimiento;

    // ✅ CÁLCULO DINÁMICO DE ESTADO
    // Eliminamos el mapeo a la vista y usamos lógica Java
    @Transient
    private String nivelStock;

    public String getNivelStock() {
        if (stockTotal == null)
            return "SIN_STOCK";
        if (stockTotal == 0)
            return "AGOTADO";
        if (stockMinimo != null && stockTotal <= stockMinimo)
            return "CRITICO";
        if (stockMinimo != null && stockTotal <= (stockMinimo + 5))
            return "BAJO"; // Margen de alerta
        return "OPTIMO";
    }

    @Column(name = "laboratorio_nombre")
    private String laboratorio;

    @Column(name = "categoria_nombre")
    private String categoria;

    @Column(name = "principio_activo_nombre")
    private String principioActivo;

    // ✅ NUEVO CAMPO IMAGEN
    @Column(name = "imagen_url")
    private String imagenUrl;
}