package com.legacy.pharmacy.inventario.dto;

import java.time.LocalDate;

/**
 * Interface Projection para el Dashboard de Alertas.
 * JPA mapea directamente desde la query JPQL a esta interfaz,
 * evitando la carga de entidades completas (over-fetching) y
 * el lazy-load N+1 sobre la relación Lote → Producto.
 */
public interface LoteAlertaDTO {
    Integer getId();

    String getNombreProducto();

    String getLote();

    LocalDate getFecha();

    Integer getCantidad();

    String getImagenUrl();
}
