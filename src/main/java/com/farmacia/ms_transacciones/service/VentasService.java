package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.dto.VentaRequestDTO;
import com.farmacia.ms_transacciones.entity.Venta; // O Venta

import java.util.List;
import com.farmacia.ms_transacciones.dto.DetalleRequestDTO;

public interface VentasService {
    // Métodos existentes
    Venta crearVenta(VentaRequestDTO ventaRequest, String vendedorId);
    List<Venta> obtenerTodasLasVentas();

    // Nuevos métodos
    Venta anularVenta(Long ventaId, String motivo);
    boolean validarStockDisponible(List<DetalleRequestDTO> productos);
    Venta obtenerVentaPorId(Long ventaId);
    List<Venta> obtenerVentasPorCliente(Long clienteId);
    List<Venta> obtenerVentasPorTurno(Long turnoId);
}