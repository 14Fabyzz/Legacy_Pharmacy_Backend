package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.client.InventarioClient;
import com.farmacia.ms_transacciones.config.UserContext;
import com.farmacia.ms_transacciones.dto.*;
import com.farmacia.ms_transacciones.model.*;
import com.farmacia.ms_transacciones.repository.*;
import com.farmacia.ms_transacciones.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private DetalleVentaRepository detalleVentaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private TurnoCajaRepository turnoCajaRepository;
    @Autowired private InventarioClient inventarioClient;

    @Override
    @Transactional
    public VentaResponseDTO crearVenta(CrearVentaDTO datosVenta) {

        // 1. Validar Caja (Opcional por ahora, pero recomendado)
        // Si no tienes turnos creados, comenta esta validación temporalmente para probar

        TurnoCaja turnoActual = turnoCajaRepository.findByUsuarioIdAndEstado(
                        String.valueOf(UserContext.getUserId()), "ABIERTO")
                .orElseThrow(() -> new RuntimeException("ERROR: No puedes vender. Debes abrir caja primero."));

        Venta venta = new Venta();
        venta.setNumeroFactura(UUID.randomUUID().toString());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setMetodoPago(datosVenta.getMetodoPago());
        venta.setEstado("COMPLETADA");
        venta.setTurno(turnoActual);

        if (datosVenta.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(datosVenta.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            venta.setCliente(cliente);
        }

        if (UserContext.getUserId() != null) {
            venta.setVendedorId(String.valueOf(UserContext.getUserId()));
        } else {
            venta.setVendedorId("ANONIMO");
        }

        venta = ventaRepository.save(venta);
        BigDecimal total = BigDecimal.ZERO;

        for (ItemVentaDTO item : datosVenta.getItems()) {
            // A. Consultar Inventario
            ProductoInventarioDTO prod = inventarioClient.obtenerProducto(item.getProductoId());

            // B. Crear Detalle
            DetalleVenta det = new DetalleVenta();
            det.setVenta(venta);
            det.setProductoId(item.getProductoId());
            det.setProductoNombre(prod.getNombreComercial());
            det.setPrecioUnitario(prod.getPrecioVentaBase());
            det.setCantidad(item.getCantidad());

            BigDecimal sub = prod.getPrecioVentaBase().multiply(new BigDecimal(item.getCantidad()));
            det.setSubtotal(sub);

            detalleVentaRepository.save(det);
            total = total.add(sub);

            // C. Descontar Inventario
            inventarioClient.registrarSalida(item.getProductoId(), item.getCantidad());
        }

        venta.setTotal(total);
        return mapToDTO(ventaRepository.save(venta));
    }

    private VentaResponseDTO mapToDTO(Venta v) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(v.getId());
        dto.setNumeroFactura(v.getNumeroFactura());
        dto.setFechaVenta(v.getFechaVenta());
        dto.setTotal(v.getTotal());
        dto.setMetodoPago(v.getMetodoPago());
        dto.setEstado(v.getEstado());
        if(v.getCliente() != null) dto.setClienteId(v.getCliente().getId());
        // Mapeo simple de items
        if(v.getDetalles() != null) {
            dto.setItems(v.getDetalles().stream().map(d -> {
                ItemVentaDTO i = new ItemVentaDTO();
                i.setProductoId(d.getProductoId());
                i.setCantidad(d.getCantidad());
                i.setPrecioUnitario(d.getPrecioUnitario());
                i.setSubtotal(d.getSubtotal());
                return i;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
