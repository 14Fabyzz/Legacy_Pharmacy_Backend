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

    @Autowired
    private com.farmacia.ms_transacciones.repository.DevolucionRepository devolucionRepository;

    @Autowired
    private com.farmacia.ms_transacciones.repository.DetalleDevolucionRepository detalleDevolucionRepository;

    // ID del Cliente Genérico (Mostrador) - NO permitido para medicamentos
    // controlados
    @org.springframework.beans.factory.annotation.Value("${ventas.cliente-generico-id:1}")
    private Integer clienteGenericoId;

    @Override
    @Transactional
    public VentaResponseDTO crearVenta(CrearVentaDTO datosVenta) {
        System.out.println("VENTA-PROCESO: Iniciando crearVenta para ClienteId: " + datosVenta.getClienteId());

        // 1. VALIDAR CAJA ABIERTA
        TurnoCaja turnoActual = turnoCajaRepository.findByUsuarioIdAndEstado(
                String.valueOf(UserContext.getUserId()), "ABIERTO")
                .orElseThrow(() -> new RuntimeException("ERROR: No puedes vender. Debes abrir caja primero."));

        // 2. VALIDAR MÉTODO DE PAGO
        if (datosVenta.getMetodoPago() == com.farmacia.ms_transacciones.enums.MetodoPago.TRANSFERENCIA ||
                datosVenta.getMetodoPago() == com.farmacia.ms_transacciones.enums.MetodoPago.TARJETA) {
            if (datosVenta.getReferenciaPago() == null || datosVenta.getReferenciaPago().trim().isEmpty()) {
                throw new com.farmacia.ms_transacciones.exception.BusinessException(
                        "Para pagos con Tarjeta o Transferencia, debe especificar la referencia de pago (Nro Referencia / Voucher).");
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

            // --- 🛡️ VALIDACIÓN DE SEGURIDAD: PRECIOS Y DESCUENTOS ---
            BigDecimal precioFinal = precioAUsar;
            BigDecimal descuentoSolicitado = item.getDescuento() != null ? item.getDescuento() : BigDecimal.ZERO;

            boolean precioModificado = item.getPrecioUnitario() != null
                    && item.getPrecioUnitario().compareTo(precioAUsar) != 0;
            boolean tieneDescuento = descuentoSolicitado.compareTo(BigDecimal.ZERO) > 0;

            if (precioModificado || tieneDescuento) {
                String rolUsuario = com.farmacia.ms_transacciones.config.UserContext.getUserRole();
                System.out.println(
                        "VENTA-SEGURIDAD: Evaluando permisos para alteración de precio. Rol detectado: " + rolUsuario);

                boolean tienePermiso = false;
                if (rolUsuario != null) {
                    String rolNormalizado = rolUsuario.toUpperCase();
                    if (rolNormalizado.contains("ADMIN") || rolNormalizado.contains("SUPERVISOR")) {
                        tienePermiso = true;
                    }
                }

                if (!tienePermiso) {
                    throw new com.farmacia.ms_transacciones.exception.BusinessException(
                            "Acceso Denegado: Su rol no permite alterar precios o aplicar descuentos.");
                }

                if (precioModificado) {
                    precioFinal = item.getPrecioUnitario();
                }

                // Convertir porcentaje a decimal (ej. 10 / 100 = 0.10)
                BigDecimal factorDescuento = descuentoSolicitado.divide(new BigDecimal("100"), 4,
                        java.math.RoundingMode.HALF_UP);

                // Calcular monto a descontar (ej. 4750 * 0.10 = 475)
                BigDecimal montoDescontado = precioFinal.multiply(factorDescuento);

                // Precio real pagado
                precioFinal = precioFinal.subtract(montoDescontado);

                if (precioFinal.compareTo(BigDecimal.ZERO) < 0) {
                    precioFinal = BigDecimal.ZERO;
                }
            }
            // --------------------------------------------------------

            // F. Crear Detalle con precio dinámico y validado
            DetalleVenta det = new DetalleVenta();
            det.setVenta(venta);
            det.setProductoId(item.getProductoId());
            det.setProductoNombre(prod.getNombreComercial());
            det.setCantidad(item.getCantidad());

            // Guardamos el PRECIO FINAL MÁS DESCUENTO COMO PRECIO UNITARIO REAL
            det.setPrecioUnitario(precioFinal);
            det.setDescuento(descuentoSolicitado);

            det.setTipoVenta(tipoVenta); // ← NUEVO: Registro de TipoVenta

            // Backward compatibility: mapear también al campo deprecated
            Boolean esVentaPorCajaDeprecated = (tipoVenta == TipoVenta.CAJA);
            det.setEsVentaPorCaja(esVentaPorCajaDeprecated);

            BigDecimal sub = precioFinal.multiply(new BigDecimal(item.getCantidad()));
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

                // El cálculo de IVA se realiza sobre el precio final cobrado
                BigDecimal precioUnitarioSinIva = precioFinal.divide(ivaFactor, 4, java.math.RoundingMode.HALF_UP);
                BigDecimal ivaUnitario = precioFinal.subtract(precioUnitarioSinIva);
                BigDecimal ivaTotalItem = ivaUnitario.multiply(new BigDecimal(item.getCantidad()));

                totalIva = totalIva.add(ivaTotalItem);
            }
            // --------------------------------

            // G. Descontar Inventario SOLO si es producto TANGIBLE
            if (!esServicio) {
                System.out.println("VENTA-PROCESO: Llamando a InventarioClient.registrarSalida para ProdId: "
                        + item.getProductoId());
                inventarioClient.registrarSalida(
                        item.getProductoId(),
                        item.getCantidad(),
                        turnoActual.getSucursalId(),
                        tipoVenta, // ← NUEVO: Enviar TipoVenta en lugar de Boolean
                        venta.getNumeroFactura() // ← NUEVO: Asegura la trazabilidad hasta el Kardex
                );
            }
        }

        // -------------------------------------------------------------
        // --- APLICAR REDONDEO AL PESO (SOLO EFECTIVO) ---
        BigDecimal ajusteRedondeo = BigDecimal.ZERO;

        if (com.farmacia.ms_transacciones.enums.MetodoPago.EFECTIVO.equals(datosVenta.getMetodoPago())) {
            BigDecimal factorRedondeo = new BigDecimal("50");
            BigDecimal totalRedondeado = total.divide(factorRedondeo, 0, java.math.RoundingMode.FLOOR)
                    .multiply(factorRedondeo);

            // Calcular el delta para el asiento contable (TotalCobrado -
            // SumatoriaProductos)
            ajusteRedondeo = totalRedondeado.subtract(total);

            // Sobreescribir el total con el valor redondeado que entregará el cliente
            total = totalRedondeado;
        }
        venta.setAjusteRedondeo(ajusteRedondeo);
        venta.setTotalIva(totalIva); // <-- CORRECCIÓN: Persistir el IVA en la entidad
        venta.setTotal(total);
        // -------------------------------------------------------------

        // --- LÓGICA DE PAGO Y CAMBIO ---
        if (com.farmacia.ms_transacciones.enums.MetodoPago.EFECTIVO.equals(venta.getMetodoPago())) {
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

        System.out
                .println("CAJA-ACTUALIZADA: TurnoID=" + turnoActual.getId() + " NuevoTotalVentas=" + nuevoTotalVentas);

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
        // Ojo: Si la entidad Venta ya tuviera "totalIva", podríamos obtenerlo de ahí si
        // totalIvaCalculado es null.
        if (totalIvaCalculado != null) {
            dto.setTotalIva(totalIvaCalculado.setScale(2, java.math.RoundingMode.HALF_UP));
        } else if (v.getTotalIva() != null) {
            dto.setTotalIva(v.getTotalIva().setScale(2, java.math.RoundingMode.HALF_UP));
        } else {
            dto.setTotalIva(BigDecimal.ZERO);
        }

        dto.setAjusteRedondeo(v.getAjusteRedondeo());

        // Nuevos campos
        dto.setMetodoPago(v.getMetodoPago());
        dto.setReferenciaPago(v.getReferenciaPago());
        dto.setVendedorNombre(v.getVendedorNombre());
        dto.setSucursalId(v.getSucursalId());

        if (v.getCliente() != null)
            dto.setClienteId(v.getCliente().getId());

        // Mapeo de items y construir resumenProductos
        if (v.getDetalles() != null) {
            java.util.List<String> resumen = new java.util.ArrayList<>();

            dto.setItems(v.getDetalles().stream().map(d -> {
                ItemVentaDTO i = new ItemVentaDTO();
                i.setProductoId(d.getProductoId());
                i.setCantidad(d.getCantidad());
                i.setPrecioUnitario(d.getPrecioUnitario());
                i.setDescuento(d.getDescuento());
                i.setSubtotal(d.getSubtotal());
                i.setEsVentaPorCaja(d.getEsVentaPorCaja()); // ← NUEVO CAMPO EN RESPUESTA

                // Construir string para el resumen: "Cantidad x NombreProducto"
                String nombreProd = (d.getProductoNombre() != null) ? d.getProductoNombre()
                        : "Producto " + d.getProductoId();
                resumen.add(d.getCantidad() + " x " + nombreProd);

                return i;
            }).collect(Collectors.toList()));

            dto.setResumenProductos(resumen);
        } else {
            dto.setResumenProductos(new java.util.ArrayList<>());
        }

        dto.setMontoRecibido(v.getMontoRecibido());
        dto.setCambio(v.getCambio());

        return dto;
    }

    @Override
    public java.util.List<VentaResponseDTO> obtenerHistorialVentas() {
        // En un entorno de producción, esto debería estar paginado.
        return ventaRepository.findAll().stream()
                .map(v -> mapToDTO(v, null))
                .collect(Collectors.toList());
    }

    @Override
    public java.util.List<VentaResponseDTO> obtenerHistorialVentasPorTurno(Long turnoId) {
        return ventaRepository.findByTurnoId(turnoId).stream()
                .map(v -> mapToDTO(v, null))
                .collect(Collectors.toList());
    }

    @Override
    public VentaResponseDTO obtenerVentaPorId(Long id) {
        Venta v = ventaRepository.findById(id)
                .orElseThrow(() -> new com.farmacia.ms_transacciones.exception.BusinessException(
                        "Venta no encontrada con ID: " + id));
        return mapToDTO(v, null);
    }

    @Override
    @Transactional
    public VentaResponseDTO procesarDevolucion(Long idVenta,
            com.farmacia.ms_transacciones.dto.DevolucionRequestDTO solicitud) {
        System.out.println("VENTA-DEVOLUCION: Iniciando proceso de devolucion para Venta #" + idVenta);

        // 1. OBTENER VENTA
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("No se encontró la venta con ID: " + idVenta));

        if ("DEVUELTA".equals(venta.getEstado())) {
            throw new RuntimeException("La venta ya ha sido devuelta en su totalidad.");
        }

        // 2. OBTENER TURNO ACTUAL (OBLIGATORIO)
        TurnoCaja turnoActual = turnoCajaRepository.findByUsuarioIdAndEstado(
                String.valueOf(UserContext.getUserId()), "ABIERTO")
                .orElseThrow(() -> new RuntimeException("ERROR: Debes abrir caja para procesar devoluciones."));

        // 3. CREAR CABECERA DE DEVOLUCION
        com.farmacia.ms_transacciones.model.Devolucion devolucion = new com.farmacia.ms_transacciones.model.Devolucion();
        devolucion.setVenta(venta);
        devolucion.setTurno(turnoActual);
        devolucion.setFecha(LocalDateTime.now());
        devolucion.setEstado("COMPLETADA");
        devolucion.setMotivoGeneral((solicitud.getMotivo() != null && !solicitud.getMotivo().isEmpty())
                ? solicitud.getMotivo()
                : "Devolución a solicitud del cliente");
        devolucion.setTotalDevuelto(BigDecimal.ZERO); // Se calculará abajo

        devolucionRepository.save(devolucion);

        BigDecimal montoTotalDevuelto = BigDecimal.ZERO;
        boolean requestVacio = (solicitud.getItems() == null || solicitud.getItems().isEmpty());

        if (requestVacio) {
            // Devolver todo lo que falta por devolver de TODOS los ítems de la Venta
            for (DetalleVenta det : venta.getDetalles()) {
                Integer cantYaDevuelta = detalleDevolucionRepository.sumCantidadDevueltaByDetalleVentaId(det.getId());
                int devueltoHistorico = (cantYaDevuelta != null) ? cantYaDevuelta : 0;
                int disponibleParaDevolver = det.getCantidad() - devueltoHistorico;

                if (disponibleParaDevolver > 0) {
                    montoTotalDevuelto = montoTotalDevuelto.add(
                            crearDetalleDevolucion(devolucion, det, disponibleParaDevolver, null, null, null));
                }
            }
        } else {
            // Devolución Parcial (basada estrictamente en el arreglo del DTO)
            for (com.farmacia.ms_transacciones.dto.ItemDevolucionDTO itemReq : solicitud.getItems()) {
                DetalleVenta det = venta.getDetalles().stream()
                        .filter(d -> d.getProductoId().equals(itemReq.getProductoId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException(
                                "El producto " + itemReq.getProductoId() + " no pertenece a esta venta."));

                Integer cantYaDevuelta = detalleDevolucionRepository.sumCantidadDevueltaByDetalleVentaId(det.getId());
                int devueltoHistorico = (cantYaDevuelta != null) ? cantYaDevuelta : 0;
                int cantidadDisponible = det.getCantidad() - devueltoHistorico;

                if (itemReq.getCantidad() > cantidadDisponible) {
                    throw new RuntimeException("No se puede devolver " + itemReq.getCantidad() + " del producto "
                            + det.getProductoNombre() + ". Solo hay " + cantidadDisponible
                            + " disponible para devolución.");
                }

                if (itemReq.getCantidad() > 0) {
                    montoTotalDevuelto = montoTotalDevuelto.add(
                            crearDetalleDevolucion(devolucion, det, itemReq.getCantidad(), itemReq.getMotivoDetalle(),
                                    itemReq.getDestinoProducto(), itemReq.getLoteId()));
                }
            }
        }

        if (montoTotalDevuelto.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("No hay ítems válidos para devolver.");
        }

        // 4. ACTUALIZAR TOTALES Y ESTADO
        boolean todosDevueltos = true;
        for (DetalleVenta d : venta.getDetalles()) {
            int qty = d.getCantidad();
            Integer ret = detalleDevolucionRepository.sumCantidadDevueltaByDetalleVentaId(d.getId());
            int hist = (ret != null) ? ret : 0;
            if (hist < qty) {
                todosDevueltos = false;
                break;
            }
        }

        // --- LÓGICA DE REGRESIÓN DE AJUSTE REDONDEO ---
        // Para evitar descuadrar la caja: si con esta transacción se devuelve el 100%
        // de la factura
        // (ya sea de golpe o sumado a devoluciones anteriores), el sistema debe
        // regresar
        // el diferencial generado por el redondeo en su momento.
        if (todosDevueltos && venta.getAjusteRedondeo() != null
                && venta.getAjusteRedondeo().compareTo(BigDecimal.ZERO) != 0) {
            System.out.println(
                    "VENTA-DEVOLUCION: Aplicando ajuste de redondeo histórico de la factura a la devolución final: "
                            + venta.getAjusteRedondeo());
            montoTotalDevuelto = montoTotalDevuelto.add(venta.getAjusteRedondeo());
        }

        // Validar negativos imprevistos
        if (montoTotalDevuelto.compareTo(BigDecimal.ZERO) < 0) {
            montoTotalDevuelto = BigDecimal.ZERO;
        }

        devolucion.setTotalDevuelto(montoTotalDevuelto);
        devolucionRepository.save(devolucion);

        venta.setEstado(todosDevueltos ? "DEVUELTA" : "PARCIALMENTE_DEVUELTA");
        ventaRepository.save(venta);

        // 5. GENERAR ASIENTO CONTABLE DE LA CAJA (EGRESO)
        com.farmacia.ms_transacciones.model.MovimientoCaja movReq = new com.farmacia.ms_transacciones.model.MovimientoCaja();
        movReq.setTurno(turnoActual);
        movReq.setTipo("DEVOLUCION_VENTA");
        movReq.setMonto(montoTotalDevuelto);
        movReq.setReferencia("DEV REQ #" + devolucion.getId() + " - VTA #" + idVenta);
        movReq.setDescripcion("Reembolso de Devolución #" + devolucion.getId() + ". " + devolucion.getMotivoGeneral());
        movReq.setFecha(LocalDateTime.now());
        movimientoCajaRepository.save(movReq);

        // 6. ACTUALIZAR SALDO DEL TURNO
        BigDecimal nuevoTotalVentas = (turnoActual.getTotalVentasTeorico() != null ? turnoActual.getTotalVentasTeorico()
                : BigDecimal.ZERO).subtract(montoTotalDevuelto);
        turnoActual.setTotalVentasTeorico(nuevoTotalVentas);

        BigDecimal nuevoTotalEgresos = (turnoActual.getTotalEgresos() != null ? turnoActual.getTotalEgresos()
                : BigDecimal.ZERO).add(montoTotalDevuelto);
        turnoActual.setTotalEgresos(nuevoTotalEgresos);
        turnoCajaRepository.save(turnoActual);

        System.out.println("VENTA-DEVOLUCION: Operación Completa. Mapeando ResponseDTO dinámico.");
        return mapToDTO(venta, null);
    }

    private BigDecimal crearDetalleDevolucion(
            com.farmacia.ms_transacciones.model.Devolucion devolucion, DetalleVenta detOriginal,
            int cantidadDevolver, String motivo, String destino, Long loteId) {

        // Calcular dinero antes de impuestos (o con ellos, depende de la regla del
        // unitario)
        BigDecimal subtotalRembolso = detOriginal.getPrecioUnitario().multiply(new BigDecimal(cantidadDevolver));

        com.farmacia.ms_transacciones.model.DetalleDevolucion detDev = new com.farmacia.ms_transacciones.model.DetalleDevolucion();
        detDev.setDevolucion(devolucion);
        detDev.setDetalleVenta(detOriginal);
        detDev.setCantidadDevuelta(cantidadDevolver);
        detDev.setPrecioUnitario(detOriginal.getPrecioUnitario());
        detDev.setSubtotal(subtotalRembolso);
        detDev.setMotivoDetalle(motivo);
        detDev.setDestinoProducto(destino);
        detDev.setLoteId(loteId); // En caso de que se soporte en un futuro
        detDev.setEstado("COMPLETADA");

        detalleDevolucionRepository.save(detDev);

        // Notificar Módulo Inventarios
        try {
            System.out.println("INVENTARIO_CLIENT: Notificando devolución. ProdId: " + detOriginal.getProductoId()
                    + " Cant: " + cantidadDevolver + " Tipo: " + detOriginal.getTipoVenta() + " Destino: " + destino);

            // Usamos el ID del reembolso de esta devolución, o el número original
            String refDevolucion = "DEV-" + devolucion.getId() + "-FAC-" + devolucion.getVenta().getNumeroFactura();

            inventarioClient.registrarDevolucion(detOriginal.getProductoId(), cantidadDevolver,
                    detOriginal.getTipoVenta(), destino, refDevolucion);
        } catch (Exception e) {
            throw new RuntimeException("Error notificando MS-Inventario para el ítem " + detOriginal.getProductoNombre()
                    + " : " + e.getMessage());
        }

        return subtotalRembolso;
    }
}