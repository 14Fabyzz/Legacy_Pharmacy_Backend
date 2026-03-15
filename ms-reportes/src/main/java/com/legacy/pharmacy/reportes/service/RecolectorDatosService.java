package com.legacy.pharmacy.reportes.service;

import com.legacy.pharmacy.reportes.client.InventarioClient;
import com.legacy.pharmacy.reportes.client.VentasClient;
import com.legacy.pharmacy.reportes.dto.internal.InventarioRawDTO;
import com.legacy.pharmacy.reportes.dto.internal.VentasRawDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@Service
public class RecolectorDatosService {

    private static final Logger log = LoggerFactory.getLogger(RecolectorDatosService.class);

    private final VentasClient ventasClient;
    private final InventarioClient inventarioClient;

    public RecolectorDatosService(VentasClient ventasClient, InventarioClient inventarioClient) {
        this.ventasClient = ventasClient;
        this.inventarioClient = inventarioClient;
    }

    /**
     * Obtiene los datos crudos de ventas e inventario de forma asíncrona.
     * Retorna un CompletableFuture genérico por ahora, pero en el futuro
     * procesará ambos DTOs combinados.
     */
    public CompletableFuture<Void> recolectarMapearDatosAsincronamente(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        log.info("Iniciando la recolección asíncrona de datos para la sucursal {} en el rango {} - {}", sucursalId, inicio, fin);

        CompletableFuture<VentasRawDTO> ventasFuture = CompletableFuture.supplyAsync(() -> 
                ventasClient.obtenerDatosCrudosVentas(inicio, fin, sucursalId)
        );

        CompletableFuture<InventarioRawDTO> inventarioFuture = CompletableFuture.supplyAsync(() -> 
                inventarioClient.obtenerDatosCrudosInventario(inicio, fin, sucursalId)
        );

        return CompletableFuture.allOf(ventasFuture, inventarioFuture).thenAccept(v -> {
            try {
                VentasRawDTO ventasRawDTO = ventasFuture.join();
                InventarioRawDTO inventarioRawDTO = inventarioFuture.join();
                
                log.info("Recolección exitosa. Ventas: {} transacciones. Inventario: {} recibido.", 
                        ventasRawDTO.getNumeroTransacciones(), inventarioRawDTO.getUnidadesRecibidas());
                        
                // Aquí el futuro proceso de mapeo usará estos datos
                
            } catch (Exception e) {
                log.error("Error al procesar los resultados asíncronos de Ventas e Inventario", e);
                // Aquí se puede propagar la excepción según requerimientos de resiliencia final
                throw e;
            }
        });
    }
}
