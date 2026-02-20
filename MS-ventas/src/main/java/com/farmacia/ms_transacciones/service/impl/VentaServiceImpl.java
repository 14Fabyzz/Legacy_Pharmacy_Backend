package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.client.InventarioClient;
import com.farmacia.ms_transacciones.config.UserContext;
import com.farmacia.ms_transacciones.dto.CrearVentaDTO;
import com.farmacia.ms_transacciones.dto.ItemVentaDTO;
import com.farmacia.ms_transacciones.dto.ProductoInventarioDTO;
import com.farmacia.ms_transacciones.dto.VentaResponseDTO;
import com.farmacia.ms_transacciones.enums.TipoVenta;
import com.farmacia.ms_transacciones.model.Cliente;
import com.farmacia.ms_transacciones.model.DetalleVenta;
import com.farmacia.ms_transacciones.model.TurnoCaja;
import com.farmacia.ms_transacciones.model.Venta;
import com.farmacia.ms_transacciones.repository.ClienteRepository;
import com.farmacia.ms_transacciones.repository.DetalleVentaRepository;
import com.farmacia.ms_transacciones.repository.TurnoCajaRepository;
import com.farmacia.ms_transacciones.repository.VentaRepository;
import com.farmacia.ms_transacciones.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TurnoCajaRepository turnoCajaRepository;
    @Autowired
    private InventarioClient inventarioClient;
    @Autowired
    private com.farmacia.ms_transacciones.repository.MovimientoCajaRepository movimientoCajaRepository;

    // ID del Cliente Genérico (Mostrador) - NO permitido para medicamentos
    // controlados
    @org.springframework.beans.factory.annotation.Value("${ventas.cliente-generico-id:1}")
    private Integer clienteGenericoId;

    @Override
    @Transactional
    public VentaResponseDTO crearVenta(CrearVentaDTO datosVenta) {
        log.info("VENTA-PROCESO: Iniciando crearVenta para ClienteId: {}", datosVenta.getClienteId());

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

        // 3. CREAR VENTA CABECERA
        Venta venta = new Venta();
        venta.setNumeroFactura(UUID.randomUUID().toString());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado("COMPLETADA");
        venta.setMetodoPago(datosVenta.getMetodoPago());
        venta.setReferenciaPago(datosVenta.getReferenciaPago());
        venta.setTurno(turnoActual);
        venta.setSucursalId(turnoActual.getSucursalId());

        // Datos del Vendedor
        if (UserContext.getUserId() != null) {
            venta.setVendedorId(String.valueOf(UserContext.getUserId()));
            venta.setVendedorNombre(UserContext.getUsername());
        } else {
            venta.setVendedorId("ANONIMO");
            venta.setVendedorNombre("Cajero Genérico");
        }

        // Cliente
        if (datosVenta.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(datosVenta.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            venta.setCliente(cliente);
        }

        venta = ventaRepository.save(venta);

        // --- LÓGICA DE DETALLES CON PRECIO DUAL ---
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO; // <-- ACUMULADOR DE IVA

        for (ItemVentaDTO item : datosVenta.getItems()) {
            // A. Consultar Inventario
            ProductoInventarioDTO prod = inventarioClient.obtenerProducto(item.getProductoId());

            // --- 🛡️ VALIDACIÓN LEGAL: MEDICAMENTOS CONTROLADOS ---
            if (Boolean.TRUE.equals(prod.getEsControlado())) {
                Long clienteVenta = datosVenta.getClienteId();

                // Si no hay cliente (null) o es el Cliente Genérico (ID 1), BLOQUEAR.
                // Esto obliga al cajero a cambiar el cliente por una persona real con cédula.
                if (clienteVenta == null || clienteVenta.equals(clienteGenericoId.longValue())) {
                    throw new com.farmacia.ms_transacciones.exception.BusinessException(
                            String.format("⛔ BLOQUEO LEGAL: El producto '%s' es CONTROLADO. " +
                                    "La ley prohíbe su venta a 'Cliente Mostrador'. " +
                                    "Acción: Asocie un cliente real con Cédula y Nombre a esta venta.",
                                    prod.getNombreComercial()));
                }
            }
            // -----------------------------------------------------

            // B. Verificar si es producto TANGIBLE o SERVICIO
            boolean esServicio = "SERVICIO".equalsIgnoreCase(prod.getTipo());

            // C. Determinar TipoVenta (Prioridad: Enum > Boolean legacy)
            TipoVenta tipoVenta = item.getTipoVenta();
            if (tipoVenta == null) {
                // Backward compatibility: convertir esVentaPorCaja a TipoVenta
                @SuppressWarnings("deprecation")
                Boolean esVentaPorCaja = item.getEsVentaPorCaja();
                tipoVenta = Boolean.TRUE.equals(esVentaPorCaja) ? TipoVenta.CAJA : TipoVenta.UNIDAD;
            }

            // D. Calcular precio según TipoVenta
            BigDecimal precioAUsar;
            switch (tipoVenta) {
                case CAJA:
                    // NUEVO: Usar precio TOTAL (con IVA) si existe
                    if (prod.getPrecioVentaTotal() != null
                            && prod.getPrecioVentaTotal().compareTo(BigDecimal.ZERO) > 0) {
                        precioAUsar = prod.getPrecioVentaTotal();
                    } else {
                        // Fallback: Calcular basándose en precioBase + IVA (si existe)
                        BigDecimal base = prod.getPrecioVentaBase();
                        if (prod.getIvaPorcentaje() != null && prod.getIvaPorcentaje().compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal ivaFactor = prod.getIvaPorcentaje()
                                    .divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
                                    .add(BigDecimal.ONE);
                            precioAUsar = base.multiply(ivaFactor).setScale(2, java.math.RoundingMode.HALF_UP);
                        } else {
                            precioAUsar = base;
                        }
                    }
                    break;

                case BLISTER:
                    // Validar que el producto tiene configuración de blister
                    if (!Boolean.TRUE.equals(prod.getEsFraccionable())) {
                        throw new RuntimeException(
                                String.format("El producto '%s' no permite venta fraccionada por blister",
                                        prod.getNombreComercial()));
                    }
                    if (prod.getUnidadesPorBlister() == null || prod.getUnidadesPorBlister() == 0) {
                        throw new RuntimeException(
                                String.format("El producto '%s' no tiene configurado unidades por blister",
                                        prod.getNombreComercial()));
                    }
                    // Usar precio blister si existe, sino calcular
                    if (prod.getPrecioVentaBlister() != null
                            && prod.getPrecioVentaBlister().compareTo(BigDecimal.ZERO) > 0) {
                        precioAUsar = prod.getPrecioVentaBlister();
                    } else {
                        precioAUsar = prod.getPrecioVentaUnidad()
                                .multiply(new BigDecimal(prod.getUnidadesPorBlister()));
                    }
                    break;

                case UNIDAD:
                    if (Boolean.TRUE.equals(prod.getEsFraccionable())) {
                        precioAUsar = prod.getPrecioVentaUnidad();
                    } else {
                        // Producto no fraccionable, forzar venta por caja (con IVA)
                        if (prod.getPrecioVentaTotal() != null
                                && prod.getPrecioVentaTotal().compareTo(BigDecimal.ZERO) > 0) {
                            precioAUsar = prod.getPrecioVentaTotal();
                        } else {
                            precioAUsar = prod.getPrecioVentaBase();
                        }
                        tipoVenta = TipoVenta.CAJA; // Override para consistencia
                    }
                    break;

                default:
                    throw new RuntimeException("TipoVenta no soportado: " + tipoVenta);
            }

            // E. Validar stock SOLO si es producto TANGIBLE
            if (!esServicio) {
                if (prod.getStockActual() < item.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + prod.getNombreComercial() +
                            ". Disponible: " + prod.getStockActual() + ", Solicitado: " + item.getCantidad());
                }
            }

            // F. Crear Detalle con precio dinámico
            DetalleVenta det = new DetalleVenta();
            det.setVenta(venta);
            det.setProductoId(item.getProductoId());
            det.setProductoNombre(prod.getNombreComercial());
            det.setCantidad(item.getCantidad());
            det.setPrecioUnitario(precioAUsar); // ← PRECIO DINÁMICO
            det.setTipoVenta(tipoVenta); // ← NUEVO: Registro de TipoVenta

            // Backward compatibility: mapear también al campo deprecated
            @SuppressWarnings("deprecation")
            Boolean esVentaPorCajaDeprecated = (tipoVenta == TipoVenta.CAJA);
            det.setEsVentaPorCaja(esVentaPorCajaDeprecated);

            BigDecimal sub = precioAUsar.multiply(new BigDecimal(item.getCantidad()));
            det.setSubtotal(sub);

            detalleVentaRepository.save(det);
            total = total.add(sub);

            // --- CÁLCULO DE IVA ACUMULADO ---
            // Si el producto tiene IVA configurado, calculamos cuánto del subtotal
            // corresponde al impuesto.
            if (prod.getIvaPorcentaje() != null && prod.getIvaPorcentaje().compareTo(BigDecimal.ZERO) > 0) {
                // precioSinIva = precioConIva / (1 + %IVA/100)
                // montoIva = precioConIva - precioSinIva
                BigDecimal ivaFactor = prod.getIvaPorcentaje()
                        .divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
                        .add(BigDecimal.ONE);

                // El precioAUsar YA INCLUYE IVA (según lógica de caja/unidad con impuestos)
                BigDecimal precioUnitarioSinIva = precioAUsar.divide(ivaFactor, 4, java.math.RoundingMode.HALF_UP);
                BigDecimal ivaUnitario = precioAUsar.subtract(precioUnitarioSinIva);
                BigDecimal ivaTotalItem = ivaUnitario.multiply(new BigDecimal(item.getCantidad()));

                totalIva = totalIva.add(ivaTotalItem);
            }
            // --------------------------------

            // G. Descontar Inventario SOLO si es producto TANGIBLE
            if (!esServicio) {
                log.info("VENTA-PROCESO: Llamando a InventarioClient.registrarSalida para ProdId: {}",
                        item.getProductoId());
                inventarioClient.registrarSalida(
                        item.getProductoId(),
                        item.getCantidad(),
                        turnoActual.getSucursalId(),
                        tipoVenta // ← NUEVO: Enviar TipoVenta en lugar de Boolean
                );
            }
        }

        venta.setTotal(total);

        // --- LÓGICA DE PAGO Y CAMBIO ---
        if ("EFECTIVO".equalsIgnoreCase(venta.getMetodoPago())) {
            if (datosVenta.getMontoRecibido() == null) {
                throw new RuntimeException("En pagos en efectivo debe indicar el monto recibido.");
            }
            if (datosVenta.getMontoRecibido().compareTo(total) < 0) {
                throw new RuntimeException(
                        "Dinero insuficiente. Faltan: " + total.subtract(datosVenta.getMontoRecibido()));
            }
            venta.setMontoRecibido(datosVenta.getMontoRecibido());
            venta.setCambio(datosVenta.getMontoRecibido().subtract(total));
        } else {
            venta.setMontoRecibido(total);
            venta.setCambio(BigDecimal.ZERO);
        }

        ventaRepository.save(venta);

        // =================================================================================
        // ACTUALIZACIÓN DE CAJA (MOVIMIENTO + SALDO) - REQUERIMIENTO CRÍTICO
        // =================================================================================

        // 1. Registrar Movimiento de Caja (Ingreso)
        com.farmacia.ms_transacciones.model.MovimientoCaja mov = new com.farmacia.ms_transacciones.model.MovimientoCaja();
        mov.setTurno(turnoActual);
        mov.setTipo("INGRESO_VENTA");
        mov.setMonto(venta.getTotal());
        mov.setReferencia("VENTA #" + venta.getId()); // Usar ID interno o NumeroFactura según preferencia
        mov.setDescripcion("Ingreso por Venta ID: " + venta.getId());
        mov.setFecha(LocalDateTime.now());
        movimientoCajaRepository.save(mov);

        // 2. Actualizar Saldo Teórico del Turno
        // Se asume que totalVentasTeorico inicial es 0 o acumulado.
        BigDecimal nuevoTotalVentas = turnoActual.getTotalVentasTeorico().add(venta.getTotal());
        turnoActual.setTotalVentasTeorico(nuevoTotalVentas);
        turnoCajaRepository.save(turnoActual);

        log.info("CAJA-ACTUALIZADA: TurnoID={} NuevoTotalVentas={}", turnoActual.getId(), nuevoTotalVentas);

        return mapToDTO(venta, totalIva);
    }

    private VentaResponseDTO mapToDTO(Venta v, BigDecimal totalIvaCalculado) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(v.getId());
        dto.setNumeroFactura(v.getNumeroFactura());
        dto.setFechaVenta(v.getFechaVenta());
        dto.setTotal(v.getTotal());
        dto.setEstado(v.getEstado());

        // Asignar IVA calculado (se pasa desde el método crearVenta porque no se guarda
        // en BD Venta)
        dto.setTotalIva(totalIvaCalculado != null ? totalIvaCalculado.setScale(2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        // Nuevos campos
        dto.setMetodoPago(v.getMetodoPago());
        dto.setReferenciaPago(v.getReferenciaPago());
        dto.setVendedorNombre(v.getVendedorNombre());
        dto.setSucursalId(v.getSucursalId());

        if (v.getCliente() != null)
            dto.setClienteId(v.getCliente().getId());

        // Mapeo de items
        if (v.getDetalles() != null) {
            dto.setItems(v.getDetalles().stream().map(d -> {
                ItemVentaDTO i = new ItemVentaDTO();
                i.setProductoId(d.getProductoId());
                i.setCantidad(d.getCantidad());
                i.setPrecioUnitario(d.getPrecioUnitario());
                i.setSubtotal(d.getSubtotal());
                i.setEsVentaPorCaja(d.getEsVentaPorCaja()); // ← NUEVO CAMPO EN RESPUESTA
                return i;
            }).collect(Collectors.toList()));
        }

        dto.setMontoRecibido(v.getMontoRecibido());
        dto.setCambio(v.getCambio());

        return dto;
    }
}