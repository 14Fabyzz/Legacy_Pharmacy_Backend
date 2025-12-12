package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.dto.request.DevolucionRequestDTO;
import com.farmacia.ms_transacciones.dto.request.DevolucionDetalleRequestDTO; // Necesario para el bucle for
import com.farmacia.ms_transacciones.dto.response.DevolucionResponseDTO;
import com.farmacia.ms_transacciones.dto.response.DetalleDevolucionResponseDTO;
import com.farmacia.ms_transacciones.entity.DetalleDevolucion;
import com.farmacia.ms_transacciones.entity.DetalleVenta;
import com.farmacia.ms_transacciones.entity.Devolucion;
import com.farmacia.ms_transacciones.entity.Venta;
import com.farmacia.ms_transacciones.repository.DetalleDevolucionRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevolucionServiceImpl implements DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final VentasRepository ventasRepository;
    private final DetalleDevolucionRepository detalleDevolucionRepository;

    @Override
    @Transactional
    public DevolucionResponseDTO crearDevolucion(DevolucionRequestDTO request) {
        // 1. Validar que la venta existe
        Venta venta = ventasRepository.findById(request.getVentaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Venta no encontrada con ID: " + request.getVentaId()));

        // 2. Preparar la cabecera de la devolución
        Devolucion devolucion = new Devolucion();
        devolucion.setVenta(venta);
        devolucion.setNumeroDevolucion(generarNumeroDevolucion());
        devolucion.setFechaDevolucion(LocalDateTime.now());
        devolucion.setMotivo(request.getMotivo());
        devolucion.setDescripcionMotivo(request.getDescripcionMotivo());
        devolucion.setTipoReembolso(request.getTipoReembolso());
        devolucion.setEstado("PENDIENTE");
        devolucion.setCreatedAt(LocalDateTime.now());

        // 3. Procesar los detalles y calcular el total
        BigDecimal totalDevolucion = BigDecimal.ZERO;
        List<DetalleDevolucion> detallesParaGuardar = new ArrayList<>();

        // Recorremos los productos que el cliente quiere devolver
        for (DevolucionDetalleRequestDTO itemRequest : request.getDetalles()) {

            // Buscamos el producto en la venta original (DetalleVenta)
            DetalleVenta detalleOriginal = venta.getDetalles().stream()
                    .filter(d -> d.getProductoId().equals(itemRequest.getProductoId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El producto ID " + itemRequest.getProductoId() + " no pertenece a esta venta."));

            // Validar cantidad (no devolver más de lo comprado)
            if (itemRequest.getCantidad() > detalleOriginal.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se puede devolver una cantidad mayor a la vendida para el producto: " + detalleOriginal.getProductoNombre());
            }

            // Calcular subtotal de este item (Precio Original * Cantidad a devolver)
            BigDecimal subtotalItem = detalleOriginal.getPrecioUnitario()
                    .multiply(new BigDecimal(itemRequest.getCantidad()));

            // Crear entidad DetalleDevolucion
            DetalleDevolucion detalleDev = new DetalleDevolucion();
            detalleDev.setDevolucion(devolucion);
            detalleDev.setDetalleVenta(detalleOriginal);
            detalleDev.setCantidad(itemRequest.getCantidad());
            detalleDev.setPrecioUnitario(detalleOriginal.getPrecioUnitario());
            detalleDev.setSubtotal(subtotalItem);
            detalleDev.setMotivoDetalle(itemRequest.getMotivoDetalle());
            detalleDev.setEstado("PENDIENTE");
            detalleDev.setDestinoProducto("REINGRESO");

            detallesParaGuardar.add(detalleDev);
            totalDevolucion = totalDevolucion.add(subtotalItem);
        }

        // 4. Asignar el total calculado (Solución al error NOT NULL)
        devolucion.setTotalDevolucion(totalDevolucion);

        // 5. Guardar todo en BD
        Devolucion devolucionGuardada = devolucionRepository.save(devolucion);

        // Asignar el ID de la devolución a cada detalle antes de guardar
        detallesParaGuardar.forEach(d -> d.setDevolucion(devolucionGuardada));
        detalleDevolucionRepository.saveAll(detallesParaGuardar);

        return convertirADTO(devolucionGuardada);
    }

    // --- MÉTODOS DE CONSULTA Y AUXILIARES ---

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
        // Nota: Asumiendo que findByVentaId devuelve List<Devolucion>
        List<DevolucionResponseDTO> list = devolucionRepository.findByVentaId(ventaId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
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
        if (estadoActual.equals("COMPLETADA") && !nuevoEstado.equals("ANULADA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cambiar el estado de una devolución completada");
        }
    }

    /**
     * Mapea la entidad Devolucion a su DTO de respuesta, incluyendo los detalles.
     */
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

        // ------------------ SECCIÓN CORREGIDA DEL DTO ------------------
        // 1. Traer los detalles de la BD
        List<DetalleDevolucion> detalles = detalleDevolucionRepository.findByDevolucionId(devolucion.getId());

        // 2. Mapear los detalles a su DTO de respuesta
        List<DetalleDevolucionResponseDTO> detallesDTO = detalles.stream()
                .map(d -> {
                    DetalleDevolucionResponseDTO det = new DetalleDevolucionResponseDTO();

                    // Usamos el ID de la tabla detalle_ventas (ID del detalle de la venta original)
                    if (d.getDetalleVenta() != null) {
                        det.setDetalleVentaId(d.getDetalleVenta().getId());
                        det.setProductoNombre(d.getDetalleVenta().getProductoNombre());
                        // NO usamos setProductoId porque no existe en el DTO
                    }

                    det.setId(d.getId());
                    det.setCantidad(d.getCantidad());
                    det.setPrecioUnitario(d.getPrecioUnitario());
                    det.setSubtotal(d.getSubtotal());
                    det.setMotivoDetalle(d.getMotivoDetalle());
                    det.setEstado(d.getEstado());
                    det.setDestinoProducto(d.getDestinoProducto());

                    return det;
                })
                .collect(Collectors.toList());

        dto.setDetalles(detallesDTO);
        // --------------------------------------------------------------

        return dto;
    }
}