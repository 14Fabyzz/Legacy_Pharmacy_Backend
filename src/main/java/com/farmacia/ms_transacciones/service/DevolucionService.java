package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.dto.DevolucionRequestDTO;
import com.farmacia.ms_transacciones.dto.response.DevolucionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DevolucionService {
    DevolucionResponseDTO crearDevolucion(DevolucionRequestDTO request);
    DevolucionResponseDTO obtenerPorId(Long id);
    Page<DevolucionResponseDTO> listarDevoluciones(Pageable pageable);
    Page<DevolucionResponseDTO> buscarPorVenta(Long ventaId, Pageable pageable);
    DevolucionResponseDTO actualizarEstado(Long id, String nuevoEstado);
}
