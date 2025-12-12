package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.entity.DetalleDevolucion;
import com.farmacia.ms_transacciones.dto.response.DetalleDevolucionResponseDTO;
import java.util.List;

public interface DetalleDevolucionService {
    DetalleDevolucion crearDetalleDevolucion(DetalleDevolucion detalle);
    DetalleDevolucion actualizarDetalleDevolucion(Long id, DetalleDevolucion detalle);
    DetalleDevolucionResponseDTO obtenerPorId(Long id);
    List<DetalleDevolucionResponseDTO> obtenerPorDevolucion(Long devolucionId);
    void eliminarDetalleDevolucion(Long id);
    List<DetalleDevolucionResponseDTO> obtenerPorEstado(String estado);
}