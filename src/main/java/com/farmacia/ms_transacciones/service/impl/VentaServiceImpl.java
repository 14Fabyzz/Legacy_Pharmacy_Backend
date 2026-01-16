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

        // 1. VALIDAR CAJA ABIERTA
        TurnoCaja turnoActual = turnoCajaRepository.findByUsuarioIdAndEstado(
                String.valueOf(UserContext.getUserId()), "ABIERTO")
                .orElseThrow(() -> new RuntimeException("ERROR: No puedes vender. Debes abrir caja primero."));

        // 2. VALIDAR MÉTODO DE PAGO
        if ("TRANSFERENCIA".equalsIgnoreCase(datosVenta.getMetodoPago())) {
            if (datosVenta.getReferenciaPago() == null || datosVenta.getReferenciaPago().trim().isEmpty()) {
                throw new RuntimeException("Para pagos con Transferencia, debe especificar el destino/referencia.");
            }
        }

        // 3. CREAR VENTA Y LLENAR DATOS
        Venta venta = new Venta();
        venta.setNumeroFactura(UUID.randomUUID().toString());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado("COMPLETADA");

        // Asignar Pago
        venta.setMetodoPago(datosVenta.getMetodoPago());
        venta.setReferenciaPago(datosVenta.getReferenciaPago()); // Puede ser null si es efectivo

        // Asignar Datos de Auditoría (Quién y Dónde)
        venta.setTurno(turnoActual);
        venta.setSucursalId(turnoActual.getSucursalId()); // La sucursal viene del turno

        // Datos del Vendedor (Token)
        if (UserContext.getUserId() != null) {
            venta.setVendedorId(String.valueOf(UserContext.getUserId()));
            venta.setVendedorNombre(UserContext.getUsername()); // Guardamos el nombre para el voucher
        } else {
            venta.setVendedorId("ANONIMO");
            venta.setVendedorNombre("Cajero Genérico");
        }

        // Lógica de Cliente
        if (datosVenta.getClienteId() != null) {
             Cliente cliente = clienteRepository.findById(datosVenta.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            venta.setCliente(cliente);
        }

        venta = ventaRepository.save(venta);

        // Lógica de Detalles e Inventario
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

        // --- LÓGICA DE PAGO Y CAMBIO ---
        if ("EFECTIVO".equalsIgnoreCase(venta.getMetodoPago())) {
            // Validar que hayan enviado cuánto pagó
            if (datosVenta.getMontoRecibido() == null) {
                throw new RuntimeException("En pagos en efectivo debe indicar el monto recibido.");
            }
            
            // Validar que el dinero alcance
            if (datosVenta.getMontoRecibido().compareTo(total) < 0) {
                throw new RuntimeException("Dinero insuficiente. Faltan: " + total.subtract(datosVenta.getMontoRecibido()));
            }

            // Calcular cambio
            venta.setMontoRecibido(datosVenta.getMontoRecibido());
            venta.setCambio(datosVenta.getMontoRecibido().subtract(total));
        } else {
            // Si es transferencia, no hay cambio
            venta.setMontoRecibido(total);
            venta.setCambio(BigDecimal.ZERO);
        }
        // -------------------------------

        return mapToDTO(ventaRepository.save(venta));
    }

    private VentaResponseDTO mapToDTO(Venta v) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(v.getId());
        dto.setNumeroFactura(v.getNumeroFactura());
        dto.setFechaVenta(v.getFechaVenta());
        dto.setTotal(v.getTotal());
        dto.setEstado(v.getEstado());

        // Nuevos campos
        dto.setMetodoPago(v.getMetodoPago());
        dto.setReferenciaPago(v.getReferenciaPago());
        dto.setVendedorNombre(v.getVendedorNombre());
        dto.setSucursalId(v.getSucursalId());

        if(v.getCliente() != null) dto.setClienteId(v.getCliente().getId());
        
        // Mapeo de items
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
        
        dto.setMontoRecibido(v.getMontoRecibido());
        dto.setCambio(v.getCambio());
        
        return dto;
    }
}