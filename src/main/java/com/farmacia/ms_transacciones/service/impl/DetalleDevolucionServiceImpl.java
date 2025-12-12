package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.entity.DetalleDevolucion;
import com.farmacia.ms_transacciones.repository.DetalleDevolucionRepository;
import com.farmacia.ms_transacciones.service.DetalleDevolucionService;
import com.farmacia.ms_transacciones.dto.response.DetalleDevolucionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetalleDevolucionServiceImpl implements DetalleDevolucionService {

    @Autowired
    private DetalleDevolucionRepository detalleDevolucionRepository;

    @Override
    @Transactional
    public DetalleDevolucion crearDetalleDevolucion(DetalleDevolucion detalle) {
        return detalleDevolucionRepository.save(detalle);
    }

    @Override
    @Transactional
    public DetalleDevolucion actualizarDetalleDevolucion(Long id, DetalleDevolucion detalle) {
        DetalleDevolucion existente = detalleDevolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de devolución no encontrado"));

        existente.setCantidad(detalle.getCantidad());
        existente.setPrecioUnitario(detalle.getPrecioUnitario());
        existente.setSubtotal(detalle.getSubtotal());
        existente.setMotivoDetalle(detalle.getMotivoDetalle());
        existente.setEstado(detalle.getEstado());
        existente.setDestinoProducto(detalle.getDestinoProducto());

        return detalleDevolucionRepository.save(existente);
    }

    @Override
    public DetalleDevolucionResponseDTO obtenerPorId(Long id) {
        DetalleDevolucion detalle = detalleDevolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de devolución no encontrado"));
        return convertirADTO(detalle);
    }

    @Override
    public List<DetalleDevolucionResponseDTO> obtenerPorDevolucion(Long devolucionId) {
        return detalleDevolucionRepository.findByDevolucionId(devolucionId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarDetalleDevolucion(Long id) {
        detalleDevolucionRepository.deleteById(id);
    }

    // --- CORRECCIÓN 1: Método obtenerPorEstado IMPLEMENTADO ---
    @Override
    public List<DetalleDevolucionResponseDTO> obtenerPorEstado(String estado) {
        return detalleDevolucionRepository.findByEstado(estado)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // --- CORRECCIÓN 2: Método convertirADTO AJUSTADO A TU DTO ---
    private DetalleDevolucionResponseDTO convertirADTO(DetalleDevolucion detalle) {
        DetalleDevolucionResponseDTO dto = new DetalleDevolucionResponseDTO();

        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        dto.setMotivoDetalle(detalle.getMotivoDetalle());
        dto.setEstado(detalle.getEstado());
        dto.setDestinoProducto(detalle.getDestinoProducto());

        // Ajuste: Usamos los campos que SÍ existen en tu DTO
        if (detalle.getDetalleVenta() != null) {
            dto.setDetalleVentaId(detalle.getDetalleVenta().getId());
            dto.setProductoNombre(detalle.getDetalleVenta().getProductoNombre());
        }

        return dto;
    }
}