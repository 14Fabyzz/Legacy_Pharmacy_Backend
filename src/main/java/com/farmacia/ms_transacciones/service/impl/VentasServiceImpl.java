package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.dto.DetalleRequestDTO; // Asegúrate de tener este import
import com.farmacia.ms_transacciones.dto.ProductoDTO;
import com.farmacia.ms_transacciones.dto.VentaRequestDTO;
import com.farmacia.ms_transacciones.entity.Cliente;
import com.farmacia.ms_transacciones.entity.DetalleVenta;
import com.farmacia.ms_transacciones.entity.TurnoCaja;
import com.farmacia.ms_transacciones.entity.Venta;
import com.farmacia.ms_transacciones.feign.InventarioClient;
import com.farmacia.ms_transacciones.repository.TurnoCajaRepository;
import com.farmacia.ms_transacciones.repository.VentasRepository;
import com.farmacia.ms_transacciones.service.VentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentasServiceImpl implements VentasService {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private TurnoCajaRepository turnoCajaRepository;

    @Autowired
    private InventarioClient inventarioClient;

    // --- 1. CREAR VENTA (Existente) ---
    @Override
    @Transactional
    public Venta crearVenta(VentaRequestDTO request, String vendedorId) {

        // A. VALIDACIÓN DE TURNO
        TurnoCaja turno = turnoCajaRepository.findByUsuarioIdAndEstado(vendedorId, "abierto")
                .orElseThrow(() -> new RuntimeException("No tienes un turno de caja abierto."));

        // B. CABECERA
        Venta venta = new Venta();
        venta.setTurnoId(turno.getId());
        venta.setVendedorId(vendedorId);
        venta.setSucursalId(request.getSucursalId());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setNumeroFactura("FAC-" + System.currentTimeMillis());
        venta.setEstado("completada");
        venta.setFormaPago(request.getFormaPago());

        if (request.getClienteId() != null) {
            Cliente cliente = new Cliente();
            cliente.setId(request.getClienteId());
            venta.setCliente(cliente);
        }

        // C. DETALLES
        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal totalVenta = BigDecimal.ZERO;

        for (var itemRequest : request.getProductos()) {
            ProductoDTO infoProducto = inventarioClient.obtenerProducto(itemRequest.getProductoId());

            if (infoProducto.getStock() < itemRequest.getCantidad()) {
                throw new RuntimeException("Stock insuficiente: " + infoProducto.getNombre());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProductoId(infoProducto.getId());
            detalle.setProductoNombre(infoProducto.getNombre());
            detalle.setProductoCodigo(infoProducto.getCodigo());
            detalle.setPrecioUnitario(infoProducto.getPrecio());
            detalle.setCantidad(itemRequest.getCantidad());
            detalle.setLoteVendido(infoProducto.getLote());

            BigDecimal subtotal = infoProducto.getPrecio().multiply(new BigDecimal(itemRequest.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setImpuesto(BigDecimal.ZERO);
            detalle.setTotal(subtotal);

            detalles.add(detalle);
            totalVenta = totalVenta.add(subtotal);
        }

        venta.setDetalles(detalles);
        venta.setSubtotal(totalVenta);
        venta.setImpuestos(BigDecimal.ZERO);
        venta.setDescuento(BigDecimal.ZERO);
        venta.setTotal(totalVenta);

        return ventasRepository.save(venta);
    }

    // --- 2. OBTENER TODAS (Existente) ---
    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerTodasLasVentas() {
        return ventasRepository.findAll();
    }

    // --- 3. ANULAR VENTA (Nuevo) ---
    @Override
    @Transactional
    public Venta anularVenta(Long ventaId, String motivo) {
        Venta venta = ventasRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + ventaId));

        if ("anulada".equalsIgnoreCase(venta.getEstado())) {
            throw new RuntimeException("Esta venta ya está anulada.");
        }

        // Cambiamos el estado
        venta.setEstado("anulada");

        // NOTA: Si tu entidad Venta tiene un campo para observaciones, úsalo aquí:
        // venta.setObservaciones("Anulada por: " + motivo);

        // TODO: Aquí deberías llamar al Microservicio de Inventario para DEVOLVER el stock
        // inventarioClient.reponerStock(venta.getDetalles());

        return ventasRepository.save(venta);
    }

    // --- 4. VALIDAR STOCK DISPONIBLE (Nuevo) ---
    @Override
    public boolean validarStockDisponible(List<DetalleRequestDTO> productos) {
        try {
            for (DetalleRequestDTO item : productos) {
                // Consultamos stock actual al MS-Inventario
                ProductoDTO info = inventarioClient.obtenerProducto(item.getProductoId());
                if (info == null || info.getStock() < item.getCantidad()) {
                    return false; // No hay stock suficiente
                }
            }
            return true; // Todos los productos tienen stock
        } catch (Exception e) {
            // Si falla la conexión con el microservicio, asumimos false por seguridad
            return false;
        }
    }

    // --- 5. OBTENER POR ID (Nuevo) ---
    @Override
    @Transactional(readOnly = true)
    public Venta obtenerVentaPorId(Long ventaId) {
        return ventasRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    // --- 6. OBTENER POR CLIENTE (Nuevo) ---
    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasPorCliente(Long clienteId) {
        // Requiere método en Repository: List<Venta> findByClienteId(Long clienteId);
        return ventasRepository.findByClienteId(clienteId);
    }

    // --- 7. OBTENER POR TURNO (Nuevo) ---
    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasPorTurno(Long turnoId) {
        // Requiere método en Repository: List<Venta> findByTurnoId(Long turnoId);
        return ventasRepository.findByTurnoId(turnoId);
    }
}