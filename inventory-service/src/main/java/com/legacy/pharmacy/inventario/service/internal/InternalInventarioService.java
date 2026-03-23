package com.legacy.pharmacy.inventario.service.internal;

import com.legacy.pharmacy.inventario.dto.internal.InventarioRawDTO;
import com.legacy.pharmacy.inventario.entity.Movimiento;
import com.legacy.pharmacy.inventario.entity.Producto;
import com.legacy.pharmacy.inventario.entity.TipoMovimiento;
import com.legacy.pharmacy.inventario.repository.MovimientoRepository;
import com.legacy.pharmacy.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InternalInventarioService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Transactional(readOnly = true)
    public InventarioRawDTO obtenerDatosCrudos(LocalDateTime inicio, LocalDateTime fin) {
        // 1. Calcular el valor del inventario actual
        List<Producto> productos = productoRepository.findAll();
        BigDecimal valorInventarioActual = BigDecimal.ZERO;
        
        for (Producto p : productos) {
            // Solo contar productos activos y con stock
            if ("ACTIVO".equals(p.getEstado()) && p.getStockActual() != null && p.getStockActual() > 0) {
                BigDecimal precioReferencia = p.getPrecioCompraReferencia() != null ? p.getPrecioCompraReferencia() : BigDecimal.ZERO;
                BigDecimal valorProducto = precioReferencia.multiply(new BigDecimal(p.getStockActual()));
                valorInventarioActual = valorInventarioActual.add(valorProducto);
            }
        }

        // 2. Calcular unidades recibidas (compras/entradas) en el rango de fechas
        List<Movimiento> entradas = movimientoRepository.findByFechaMovimientoBetweenAndTipoMovimiento(inicio, fin, TipoMovimiento.ENTRADA);
        long unidadesRecibidas = 0;
        for (Movimiento m : entradas) {
            unidadesRecibidas += m.getCantidad() != null ? m.getCantidad() : 0;
        }

        return InventarioRawDTO.builder()
                .valorInventarioActual(valorInventarioActual)
                .inventarioPromedio(valorInventarioActual) // Simplificación asumiendo inventario estable a corto plazo
                .unidadesRecibidas(unidadesRecibidas)
                .valorInventarioTeorico(valorInventarioActual)
                .valorInventarioFisico(valorInventarioActual)
                .build();
    }
}
