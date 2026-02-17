package com.legacy.pharmacy.inventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Formula; // <-- Importante para el cálculo dinámico

import java.math.BigDecimal; // Importante para dinero
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_interno", nullable = false, unique = true, length = 50)
    private String codigoInterno;

    @Column(name = "codigo_barras", unique = true, length = 100)
    private String codigoBarras;

    @Column(name = "nombre_comercial", nullable = false, length = 200)
    private String nombreComercial;

    // --- RELACIONES (Llaves Foráneas) ---

    // Un producto tiene UNA categoría
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Un producto tiene UN laboratorio
    @ManyToOne
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    // Un producto tiene UN principio activo (puede ser nulo si es un cosmético, por
    // ejemplo)
    @ManyToOne
    @JoinColumn(name = "principio_activo_id")
    private PrincipioActivo principioActivo;

    // --- DATOS TÉCNICOS ---

    @Column(length = 100)
    private String concentracion; // Ej: 500mg

    @Column(length = 100)
    private String presentacion; // Ej: Caja x 30

    @Column(name = "registro_invima", length = 100)
    private String registroInvima;

    // --- PRECIOS E INVENTARIO ---

    @Column(name = "precio_compra_referencia", nullable = false)
    private BigDecimal precioCompraReferencia = BigDecimal.ZERO;

    @Column(name = "porcentaje_ganancia", nullable = false)
    private BigDecimal porcentajeGanancia = new BigDecimal("30.00");

    @Column(name = "precio_venta_base", nullable = false)
    private BigDecimal precioVentaBase;

    @Column(name = "precio_venta_total", nullable = false)
    private BigDecimal precioVentaTotal = BigDecimal.ZERO;

    @Column(name = "iva_porcentaje")
    private BigDecimal ivaPorcentaje;

    @Column(name = "margen_minimo_porcentaje")
    private BigDecimal margenMinimoPorcentaje;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 10;

    @Column(name = "es_controlado")
    private Boolean esControlado = false;

    @Column(name = "refrigerado")
    private Boolean refrigerado = false;

    // Usamos String para el ENUM por simplicidad (ACTIVO, DESCONTINUADO)
    @Column(length = 20)
    private String estado = "ACTIVO";

    // Tipo de producto (TANGIBLE con stock vs SERVICIO sin stock)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoProducto tipo = TipoProducto.TANGIBLE;

    // --- STOCK DINÁMICO (CALCULADO) ---
    // Esta anotación ejecuta una subconsulta cada vez que se trae el producto.
    // Reemplaza la necesidad de hacer queries adicionales o tener una columna desactualizada.
    @Formula("(SELECT COALESCE(SUM(l.cantidad_actual), 0) FROM lotes l WHERE l.producto_id = id AND l.cantidad_actual > 0)")
    private Integer stockActual;

    // --- DATOS DE FRACCIONAMIENTO (NUEVO) ---

    @Column(name = "es_fraccionable")
    private Boolean esFraccionable = false;

    @Column(name = "unidades_por_caja")
    private Integer unidadesPorCaja = 1;

    @Column(name = "unidades_por_blister")
    private Integer unidadesPorBlister; // Informativo para UX (Ej: 10 pastillas/blister)

    @Column(name = "precio_venta_unidad")
    private BigDecimal precioVentaUnidad;

    @Column(name = "precio_venta_blister")
    private BigDecimal precioVentaBlister;

    // --- AUDITORÍA ---

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- IMÁGENES (Cloudinary) ---

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "imagen_id")
    private String imagenId;

    // =====================================================
    // MÉTODO DE NEGOCIO: CÁLCULO DETERMINISTA DE PRECIOS
    // =====================================================

    /**
     * Recalcula todos los precios de venta basándose en el costo y margen de
     * ganancia.
     * REGLAS DE NEGOCIO:
     * 1. Precio Base = Costo × (1 + Ganancia%)
     * 2. Precio Total = Precio Base × (1 + IVA%)
     * 3. Precio Unidad = redondearCincuentena(Precio Total / Unidades)
     * 4. Precio Blister = Precio Unidad × Unidades por Blister
     */
    public void recalcularPrecios() {
        // 0. Validaciones defensivas (división por cero, NULLs)
        if (this.precioCompraReferencia == null) {
            this.precioCompraReferencia = BigDecimal.ZERO;
        }
        if (this.porcentajeGanancia == null) {
            this.porcentajeGanancia = new BigDecimal("30.00");
        }
        if (this.ivaPorcentaje == null) {
            this.ivaPorcentaje = BigDecimal.ZERO;
        }

        // 1. Precio Base = Costo × (1 + Ganancia%)
        BigDecimal gananciaFactor = this.porcentajeGanancia
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                .add(BigDecimal.ONE);
        this.precioVentaBase = this.precioCompraReferencia
                .multiply(gananciaFactor)
                .setScale(2, RoundingMode.HALF_UP);

        // 2. Precio Total = Precio Base × (1 + IVA%)
        BigDecimal ivaFactor = this.ivaPorcentaje
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                .add(BigDecimal.ONE);
        this.precioVentaTotal = this.precioVentaBase
                .multiply(ivaFactor)
                .setScale(2, RoundingMode.HALF_UP);

        // 3. Precio Unidad (con redondeo a cincuentena si es fraccionable)
        if (this.unidadesPorCaja != null && this.unidadesPorCaja > 1) {
            // División con redondeo UP para favorecer al farmacéutico
            BigDecimal precioSinRedondear = this.precioVentaTotal
                    .divide(BigDecimal.valueOf(this.unidadesPorCaja), 2, RoundingMode.UP);
            this.precioVentaUnidad = redondearCincuentena(precioSinRedondear);
        } else {
            // Producto NO fraccionable: precio unidad = precio total
            this.precioVentaUnidad = this.precioVentaTotal;
        }

        // 4. Precio Blister (si aplica)
        if (this.unidadesPorBlister != null && this.unidadesPorBlister > 0
                && this.unidadesPorCaja != null && this.unidadesPorCaja > 1) {
            this.precioVentaBlister = this.precioVentaUnidad
                    .multiply(BigDecimal.valueOf(this.unidadesPorBlister));
        } else {
            this.precioVentaBlister = BigDecimal.ZERO;
        }
    }

    /**
     * Redondea un valor al TECHO de la cincuentena más cercana.
     * Ejemplos: 512 → 550, 560 → 600, 1230 → 1250
     * REGLA DE ORO: Favorece al farmacéutico para evitar problemas de cambio.
     */
    private BigDecimal redondearCincuentena(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        double valorDouble = valor.doubleValue();
        double redondeado = Math.ceil(valorDouble / 50.0) * 50.0;
        return BigDecimal.valueOf(redondeado);
    }
}