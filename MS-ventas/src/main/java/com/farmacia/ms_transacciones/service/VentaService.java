package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.dto.CrearVentaDTO;
import com.farmacia.ms_transacciones.dto.VentaResponseDTO;

import java.util.List;

public interface VentaService {
    VentaResponseDTO crearVenta(CrearVentaDTO datosVenta);

    List<VentaResponseDTO> obtenerHistorialVentas();

    List<VentaResponseDTO> obtenerHistorialVentasPorTurno(Long turnoId);

    VentaResponseDTO obtenerVentaPorId(Long id);

    VentaResponseDTO procesarDevolucion(Long idVenta, com.farmacia.ms_transacciones.dto.DevolucionRequestDTO solicitud);
}
