package com.farmacia.ms_transacciones.service.internal;

import com.farmacia.ms_transacciones.dto.internal.VentasRawDTO;
import com.farmacia.ms_transacciones.model.Venta;
import com.farmacia.ms_transacciones.model.DetalleVenta;
import com.farmacia.ms_transacciones.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InternalVentasService {

    @Autowired
    private VentaRepository ventaRepository;

    @Transactional(readOnly = true)
    public VentasRawDTO obtenerDatosCrudos(LocalDateTime inicio, LocalDateTime fin) {
        List<Venta> ventas = ventaRepository.findByFechaVentaBetween(inicio, fin);
        
        long numeroTransacciones = 0;
        long unidadesVendidas = 0;
        BigDecimal totalIngresos = BigDecimal.ZERO;

        for (Venta venta : ventas) {
            // Ignorar ventas anuladas o devueltas en su totalidad para no inflar ingresos
            if ("ANULADA".equals(venta.getEstado()) || "DEVUELTA".equals(venta.getEstado())) {
                continue;
            }
            
            numeroTransacciones++;
            if (venta.getTotal() != null) {
                totalIngresos = totalIngresos.add(venta.getTotal());
            }
            
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getCantidad() != null) {
                        unidadesVendidas += detalle.getCantidad();
                    }
                }
            }
        }

        VentasRawDTO dto = new VentasRawDTO();
        dto.setTotalIngresos(totalIngresos);
        // MS-ventas no almacena el costo de adquisición (COGS) por detalle, lo enviamos en 0.
        // inventory-service se encargará opcionalmente de otras métricas de costo si es necesario.
        dto.setCostoMercanciaVendidaCogs(BigDecimal.ZERO); 
        dto.setNumeroTransacciones(numeroTransacciones);
        dto.setUnidadesVendidas(unidadesVendidas);

        return dto;
    }
}
