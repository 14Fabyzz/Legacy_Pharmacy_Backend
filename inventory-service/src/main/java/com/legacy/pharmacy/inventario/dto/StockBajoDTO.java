package com.legacy.pharmacy.inventario.dto;

/**
 * Interface Projection para productos con stock bajo en el Dashboard de
 * Alertas.
 * La query nativa calcula stockActual como SUM(lotes.cantidad_actual)
 * directamente
 * en la BD, eliminando el bucle N+1 que llamaba a consultarStockActual() por
 * cada producto.
 */
public interface StockBajoDTO {
    Integer getId();

    String getNombre();

    Long getStockActual(); // SUM() en MySQL devuelve Long/BigDecimal

    Integer getStockMinimo();

    String getImagenUrl();
}
