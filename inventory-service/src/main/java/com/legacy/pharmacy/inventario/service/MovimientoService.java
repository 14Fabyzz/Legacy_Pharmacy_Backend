package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.dto.AuditoriaDTO;
import com.legacy.pharmacy.inventario.entity.Lote;
import com.legacy.pharmacy.inventario.entity.Movimiento;
import com.legacy.pharmacy.inventario.entity.TipoMovimiento;
import com.legacy.pharmacy.inventario.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    // Método para consultar el reporte (El que usará el Controlador)
    public List<AuditoriaDTO> obtenerHistorialCompleto() {
        return movimientoRepository.obtenerAuditoriaCompleta();
    }

    // Método utilitario para registrar movimientos desde otros servicios (Ventas,
    // Compras)
    @Transactional
    public void registrarMovimiento(Lote lote, TipoMovimiento tipo, Integer cantidad, String responsable,
            String observacion) {
        Movimiento movimiento = Movimiento.builder()
                .lote(lote)
                .tipoMovimiento(tipo)
                .cantidad(cantidad) // Asegúrate de mandar negativo si es salida
                .usuarioResponsable(responsable)
                .observacion(observacion)
                .build();

        movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public List<com.legacy.pharmacy.inventario.dto.MovimientoKardexDTO> obtenerKardexProducto(Integer productoId) {
        // 1. Obtener todos los movimientos ordenados por fecha ascendente
        List<Movimiento> movimientos = movimientoRepository.findByLote_Producto_IdOrderByFechaMovimientoAsc(productoId);

        // 2. Calcular saldos acumulativos
        int saldoAcumulado = 0;
        List<com.legacy.pharmacy.inventario.dto.MovimientoKardexDTO> kardex = new java.util.ArrayList<>();

        for (Movimiento m : movimientos) {
            saldoAcumulado += m.getCantidad();

            kardex.add(com.legacy.pharmacy.inventario.dto.MovimientoKardexDTO.builder()
                    .id(m.getId())
                    .fecha(m.getFechaMovimiento())
                    .tipo(m.getTipoMovimiento().name())
                    .cantidad(m.getCantidad())
                    .saldoResultante(saldoAcumulado)
                    .saldoHistorico(m.getSaldoHistorico()) // TAREA 4
                    .documentoRef(m.getDocumentoRef()) // <-- CORRECCIÓN: Usar getDocumentoRef
                    .usuario(m.getUsuarioResponsable())
                    .nombreProducto(m.getLote().getProducto().getNombreComercial())
                    .codigoBarras(m.getLote().getProducto().getCodigoBarras())
                    .detalle(m.getObservacion())
                    .lote(m.getLote().getNumeroLote())
                    .costoUnitario(m.getLote().getCostoCompra())
                    .build());
        }

        // 3. Invertir lista para mostrar lo más reciente primero (opcional, pero común
        // en UIs)
        java.util.Collections.reverse(kardex);

        return kardex;
    }

    @Transactional(readOnly = true)
    public List<com.legacy.pharmacy.inventario.dto.MovimientoKardexDTO> obtenerBitacoraReciente() {
        // 1. Obtener los últimos 50 movimientos
        // Usamos PageRequest para el LIMIT 50
        List<Movimiento> movimientos = movimientoRepository
                .findRecent(org.springframework.data.domain.PageRequest.of(0, 50));

        // 2. Mapear a DTO (Esto disparará N+1 queries por los Lazy Loads, pero es
        // seguro en Transactional)
        return movimientos.stream().map(m -> com.legacy.pharmacy.inventario.dto.MovimientoKardexDTO.builder()
                .id(m.getId())
                .fecha(m.getFechaMovimiento())
                .tipo(m.getTipoMovimiento().name())
                .cantidad(m.getCantidad())
                // porque no podemos calcular el saldo histórico parcial eficientemente en una
                // lista paginada global
                .saldoResultante(m.getLote().getCantidadActual())
                .saldoHistorico(m.getSaldoHistorico()) // TAREA 4
                .documentoRef(m.getDocumentoRef()) // <-- CORRECCIÓN: Usar getDocumentoRef
                .detalle(m.getObservacion())
                .lote(m.getLote().getNumeroLote())
                .costoUnitario(m.getLote().getCostoCompra())
                .usuario(m.getUsuarioResponsable())
                .nombreProducto(m.getLote().getProducto() != null ? m.getLote().getProducto().getNombreComercial()
                        : "PRODUCTO ELIMINADO (Lote ID: " + m.getLote().getId() + ")")
                .codigoBarras(m.getLote().getProducto() != null ? m.getLote().getProducto().getCodigoBarras() : "N/A")
                .build())
                .collect(java.util.stream.Collectors.toList());
    }
}