package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentasRepository extends JpaRepository<Venta, Long> {

    // --- MÉTODOS EXISTENTES ---

    // Para reportes o historial del vendedor
    List<Venta> findByVendedorId(String vendedorId);

    // Para el reporte de cierre de caja (HU-09.5)
    // SQL generado: SELECT * FROM venta WHERE turno_id = ?
    List<Venta> findByTurnoId(Long turnoId);

    // --- NUEVOS MÉTODOS ---

    // NECESARIO PARA EL SERVICE (método obtenerVentasPorCliente)
    // SQL generado: SELECT * FROM venta WHERE cliente_id = ?
    List<Venta> findByClienteId(Long clienteId);

    // MEJORA: Usar Optional evita NullPointerException si escriben mal la factura
    // SQL generado: SELECT * FROM venta WHERE numero_factura = ?
    Optional<Venta> findByNumeroFactura(String numeroFactura);

    // EXTRA: Útil para reportes (Ej: Ventas de hoy, Ventas del mes)
    // SQL generado: SELECT * FROM venta WHERE fecha_venta BETWEEN ? AND ?
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // EXTRA: Buscar por estado (Ej: ver solo las "anuladas")
    List<Venta> findByEstado(String estado);
}
