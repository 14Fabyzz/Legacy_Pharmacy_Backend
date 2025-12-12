package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.dto.DevolucionRequestDTO;
import com.farmacia.ms_transacciones.dto.response.DevolucionResponseDTO;
import com.farmacia.ms_transacciones.entity.Devolucion;
import com.farmacia.ms_transacciones.entity.Venta;
import com.farmacia.ms_transacciones.repository.DevolucionRepository;
import com.farmacia.ms_transacciones.repository.VentasRepository;
import com.farmacia.ms_transacciones.service.DevolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DevolucionServiceImpl implements DevolucionService {
    private final DevolucionRepository devolucionRepository;
    private final VentasRepository ventasRepository;

    @Override
    @Transactional
    public DevolucionResponseDTO crearDevolucion(DevolucionRequestDTO request) {
        // Validar que la venta existe
        Venta venta = ventasRepository.findById(request.getVentaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Venta no encontrada con ID: " + request.getVentaId()));

        Devolucion devolucion = new Devolucion();
        devolucion.setVenta(venta);
        devolucion.setNumeroDevolucion(generarNumeroDevolucion());
        devolucion.setFechaDevolucion(LocalDateTime.now());
        devolucion.setMotivo(request.getMotivo());
        devolucion.setDescripcionMotivo(request.getDescripcionMotivo());
        devolucion.setTipoReembolso(request.getTipoReembolso());
        devolucion.setEstado("PENDIENTE");
        devolucion.setCreatedAt(LocalDateTime.now());

        // Calcular el total de la devolución basado en los productos devueltos
        // TODO: Implementar cálculo del total

        Devolucion devolucionGuardada = devolucionRepository.save(devolucion);
        return convertirADTO(devolucionGuardada);
    }

    @Override
    public DevolucionResponseDTO obtenerPorId(Long id) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Devolución no encontrada con ID: " + id));
        return convertirADTO(devolucion);
    }

    @Override
    public Page<DevolucionResponseDTO> listarDevoluciones(Pageable pageable) {
        return devolucionRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<DevolucionResponseDTO> buscarPorVenta(Long ventaId, Pageable pageable) {
        return devolucionRepository.findByVentaId(ventaId)
                .stream()
                .map(this::convertirADTO)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    @Transactional
    public DevolucionResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Devolución no encontrada con ID: " + id));
        
        validarCambioEstado(devolucion.getEstado(), nuevoEstado);
        
        devolucion.setEstado(nuevoEstado);
        devolucion.setUpdatedAt(LocalDateTime.now());
        
        return convertirADTO(devolucionRepository.save(devolucion));
    }

    private String generarNumeroDevolucion() {
        return "DEV-" + System.currentTimeMillis();
    }

    private void validarCambioEstado(String estadoActual, String nuevoEstado) {
        // Implementar validaciones de cambio de estado según reglas de negocio
        if (estadoActual.equals("COMPLETADA") && !nuevoEstado.equals("ANULADA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "No se puede cambiar el estado de una devolución completada");
        }
    }

    private DevolucionResponseDTO convertirADTO(Devolucion devolucion) {
        DevolucionResponseDTO dto = new DevolucionResponseDTO();
        dto.setId(devolucion.getId());
        dto.setNumeroDevolucion(devolucion.getNumeroDevolucion());
        dto.setNumeroFactura(devolucion.getVenta().getNumeroFactura());
        dto.setFechaDevolucion(devolucion.getFechaDevolucion());
        dto.setMotivo(devolucion.getMotivo());
        dto.setDescripcionMotivo(devolucion.getDescripcionMotivo());
        dto.setTotalDevolucion(devolucion.getTotalDevolucion());
        dto.setTipoReembolso(devolucion.getTipoReembolso());
        dto.setEstado(devolucion.getEstado());
        // TODO: Agregar detalles de la devolución
        return dto;
    }
}