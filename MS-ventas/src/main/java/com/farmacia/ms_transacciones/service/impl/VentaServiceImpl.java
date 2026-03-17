package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.client.InventarioClient;
import com.farmacia.ms_transacciones.config.UserContext;
import com.farmacia.ms_transacciones.dto.CrearVentaDTO;
import com.farmacia.ms_transacciones.dto.ItemVentaDTO;
import com.farmacia.ms_transacciones.dto.ProductoInventarioDTO;
import com.farmacia.ms_transacciones.dto.VentaResponseDTO;
import com.farmacia.ms_transacciones.enums.TipoEventoAuditoria;
import com.farmacia.ms_transacciones.enums.TipoVenta;
import com.farmacia.ms_transacciones.model.BitacoraVenta;
import com.farmacia.ms_transacciones.model.Cliente;
import com.farmacia.ms_transacciones.model.DetalleVenta;
import com.farmacia.ms_transacciones.model.TurnoCaja;
import com.farmacia.ms_transacciones.model.Venta;
import com.farmacia.ms_transacciones.repository.BitacoraVentaRepository;
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

    @Autowired
    private BitacoraVentaRepository bitacoraVentaRepository;

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
        Long clienteRecepcionado = datosVenta.getClienteId();
        Long clienteIdFinal = (clienteRecepcionado != null) ? clienteRecepcionado : 1L;

        Cliente cliente = clienteRepository.findById(clienteIdFinal)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        venta.setCliente(cliente);

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

        if (v.getCliente() != null) {
            dto.setClienteId(v.getCliente().getId());
            dto.setClienteNombre(v.getCliente().getNombre());
        } else {
            dto.setClienteNombre("Consumidor Final");
        }

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

        // 1. Declarar la 'canasta' recolectora
        java.util.List<com.farmacia.ms_transacciones.dto.BatchItemDevolucionDTO> itemsParaInventario = new java.util.ArrayList<>();

        if (requestVacio) {
            // Devolver todo lo que falta por devolver de TODOS los ítems de la Venta
            for (DetalleVenta det : venta.getDetalles()) {
                Integer cantYaDevuelta = detalleDevolucionRepository.sumCantidadDevueltaByDetalleVentaId(det.getId());
                int devueltoHistorico = (cantYaDevuelta != null) ? cantYaDevuelta : 0;
                int disponibleParaDevolver = det.getCantidad() - devueltoHistorico;

                // Definir el destino por defecto si no viene a nivel DTO general
                String destinoFinal = (solicitud.getDestino() != null && !solicitud.getDestino().isEmpty())
                        ? solicitud.getDestino()
                        : "INVENTARIO_DISPONIBLE";

                if (disponibleParaDevolver > 0) {
                    // Guardado Local
                    montoTotalDevuelto = montoTotalDevuelto.add(
                            crearDetalleDevolucionLocal(devolucion, det, disponibleParaDevolver, null, destinoFinal,
                                    null));

                    // Llenar el Batch
                    com.farmacia.ms_transacciones.dto.BatchItemDevolucionDTO batchItem = new com.farmacia.ms_transacciones.dto.BatchItemDevolucionDTO();
                    batchItem.setProductoId(det.getProductoId());
                    batchItem.setCantidad(disponibleParaDevolver);
                    batchItem.setTipoVenta(det.getTipoVenta());
                    batchItem.setDestinoProducto(destinoFinal);
                    batchItem.setMotivo(solicitud.getMotivo() != null ? solicitud.getMotivo()
                            : "Devolución a solicitud del cliente");
                    batchItem.setLoteId(null);
                    itemsParaInventario.add(batchItem);
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
                    // Guardado Local
                    montoTotalDevuelto = montoTotalDevuelto.add(
                            crearDetalleDevolucionLocal(devolucion, det, itemReq.getCantidad(),
                                    itemReq.getMotivoDetalle(),
                                    itemReq.getDestinoProducto(), itemReq.getLoteId()));

                    // Llenar el Batch
                    com.farmacia.ms_transacciones.dto.BatchItemDevolucionDTO batchItem = new com.farmacia.ms_transacciones.dto.BatchItemDevolucionDTO();
                    batchItem.setProductoId(det.getProductoId());
                    batchItem.setCantidad(itemReq.getCantidad());
                    batchItem.setTipoVenta(det.getTipoVenta());
                    batchItem.setDestinoProducto(itemReq.getDestinoProducto());
                    batchItem.setMotivo(itemReq.getMotivoDetalle());
                    batchItem.setLoteId(itemReq.getLoteId());
                    itemsParaInventario.add(batchItem);
                }
            }
        }

        if (montoTotalDevuelto.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("No hay ítems válidos para devolver.");
        }

        // 2. Llamada Batch de red FUERA del bucle
        if (!itemsParaInventario.isEmpty()) {
            com.farmacia.ms_transacciones.dto.BatchDevolucionRequestDTO batchRequest = new com.farmacia.ms_transacciones.dto.BatchDevolucionRequestDTO();
            batchRequest.setDocumentoRef("DEV-" + devolucion.getId() + "-FAC-" + venta.getNumeroFactura());
            batchRequest.setItems(itemsParaInventario);

            // Un solo viaje por red
            inventarioClient.registrarDevolucionBatch(batchRequest);
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

        // 7. REGISTRAR EVENTO EN BITÁCORA DE AUDITORÍA
        BitacoraVenta bitacora = new BitacoraVenta();
        bitacora.setVentaId(idVenta);
        bitacora.setTurnoId(turnoActual.getId());
        bitacora.setUsuarioId(String.valueOf(UserContext.getUserId()));
        bitacora.setMotivo(solicitud.getMotivo() != null && !solicitud.getMotivo().isEmpty() ? solicitud.getMotivo()
                : "Devolución a solicitud del cliente");
        bitacora.setTipoEvento(
                requestVacio ? TipoEventoAuditoria.ANULACION_TOTAL : TipoEventoAuditoria.DEVOLUCION_PARCIAL);
        bitacora.setFechaEvento(LocalDateTime.now());

        // Construir JSON simple
        String jsonPayload;
        if (requestVacio) {
            jsonPayload = "{\"anulacionTotal\": true, \"montoReembolsado\": " + montoTotalDevuelto.toString() + "}";
        } else {
            java.util.List<Integer> productosDevueltos = solicitud.getItems().stream()
                    .map(com.farmacia.ms_transacciones.dto.ItemDevolucionDTO::getProductoId)
                    .collect(Collectors.toList());
            jsonPayload = "{\"anulacionTotal\": false, \"montoReembolsado\": " + montoTotalDevuelto.toString()
                    + ", \"productosDevueltos\": " + productosDevueltos.toString() + "}";
        }
        bitacora.setDetallesCambiosJson(jsonPayload);

        bitacoraVentaRepository.save(bitacora);

        System.out.println("VENTA-DEVOLUCION: Operación Completa. Mapeando ResponseDTO dinámico.");
        return mapToDTO(venta, null);
    }

    @Override
    @Transactional
    public VentaResponseDTO editarVenta(Long idVenta, com.farmacia.ms_transacciones.dto.EditarVentaDTO solicitud) {
        System.out.println("VENTA-EDICION: Iniciando edición de Venta #" + idVenta);

        // 1. OBTENER VENTA Y VALIDACIONES
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("No se encontró la venta con ID: " + idVenta));

        if ("ANULADA".equals(venta.getEstado()) || "DEVUELTA".equals(venta.getEstado())) {
            throw new RuntimeException("No se puede editar una venta que está " + venta.getEstado());
        }

        TurnoCaja turnoActual = turnoCajaRepository.findByUsuarioIdAndEstado(
                String.valueOf(UserContext.getUserId()), "ABIERTO")
                .orElseThrow(() -> new RuntimeException("ERROR: Debes abrir caja para editar ventas."));

        BigDecimal totalAntiguo = venta.getTotal();

        // 2. CONSTRUIR MAPAS Y ACUMULADORES
        java.util.Map<Integer, DetalleVenta> antiguos = venta.getDetalles().stream()
                .collect(Collectors.toMap(DetalleVenta::getProductoId, d -> d));

        java.util.Map<Integer, ItemVentaDTO> nuevos = solicitud.getItemsFinales().stream()
                .collect(Collectors.toMap(ItemVentaDTO::getProductoId, i -> i));

        BigDecimal nuevoSubtotalGlobal = BigDecimal.ZERO;
        java.util.List<DetalleVenta> detallesParaConservar = new java.util.ArrayList<>();
        java.util.List<String> logsEdicion = new java.util.ArrayList<>();

        // 3. CALCULO DE DELTAS (INVENTARIO Y FINANCIERO BASADO EN HISTÓRICO)

        // A. Elementos Eliminados (se devuelven al inventario)
        for (DetalleVenta antiguo : venta.getDetalles()) {
            if (!nuevos.containsKey(antiguo.getProductoId())) {
                try {
                    System.out.println("VENTA-EDICION: Devolviendo producto eliminado " + antiguo.getProductoNombre());
                    inventarioClient.registrarDevolucion(
                            antiguo.getProductoId(),
                            antiguo.getCantidad(),
                            antiguo.getTipoVenta(),
                            "INVENTARIO_DISPONIBLE",
                            "EDICION_VENTA_ELIMINADO_" + venta.getId());
                } catch (Exception e) {
                    System.err.println("Error notificando MS-Inventario para devolución: " + e.getMessage());
                }
                detalleVentaRepository.delete(antiguo);
                logsEdicion.add("{'accion': 'ELIMINADO', 'productoId': " + antiguo.getProductoId() + ", 'cant': "
                        + antiguo.getCantidad() + "}");
            }
        }

        // B. Elementos de la nueva solicitud
        for (ItemVentaDTO nuevoReq : solicitud.getItemsFinales()) {
            Integer prodId = nuevoReq.getProductoId();

            if (antiguos.containsKey(prodId)) {
                DetalleVenta detAntiguo = antiguos.get(prodId);
                int cantAntigua = detAntiguo.getCantidad();
                int cantNueva = nuevoReq.getCantidad();

                if (cantNueva < cantAntigua) {
                    // Devolución parcial al inventario
                    int delta = cantAntigua - cantNueva;
                    try {
                        inventarioClient.registrarDevolucion(
                                prodId, delta, detAntiguo.getTipoVenta(), "INVENTARIO_DISPONIBLE",
                                "EDICION_VENTA_REDUCCION_" + venta.getId());
                    } catch (Exception e) {
                        System.err.println("Error notificando inventario (devolucion) en edición: " + e.getMessage());
                    }
                } else if (cantNueva > cantAntigua) {
                    // Salida adicional de inventario
                    int delta = cantNueva - cantAntigua;
                    try {
                        inventarioClient.registrarSalida(
                                prodId, delta, venta.getSucursalId(), detAntiguo.getTipoVenta(),
                                "EDICION_VENTA_AUMENTO_" + venta.getId());
                    } catch (Exception e) {
                        System.err.println("Error notificando inventario (salida) en edición: " + e.getMessage());
                        throw new RuntimeException("Stock insuficiente en MS-Inventario para realizar el aumento.");
                    }
                }

                if (cantNueva != cantAntigua) {
                    logsEdicion.add("{'accion': 'MODIFICADO', 'productoId': " + prodId + ", 'de': " + cantAntigua
                            + ", 'a': " + cantNueva + "}");
                }

                // Delta Financiero Strict (MANTENIENDO PRECIO Y LÓGICA ORIGINAL)
                BigDecimal precioUnitarioHistorico = detAntiguo.getPrecioUnitario();
                BigDecimal nuevoSubtotalItem = precioUnitarioHistorico.multiply(new BigDecimal(cantNueva));

                detAntiguo.setCantidad(cantNueva);
                detAntiguo.setSubtotal(nuevoSubtotalItem);

                nuevoSubtotalGlobal = nuevoSubtotalGlobal.add(nuevoSubtotalItem);
                detallesParaConservar.add(detAntiguo);

            } else {
                // Producto Nuevo agregado durante la edición
                // 1. Validar en el Inventario (se necesita consultar el MS-Inventario para
                // precios y tipo)
                ProductoInventarioDTO prod = inventarioClient.obtenerProducto(prodId);
                if (prod.getStockActual() < nuevoReq.getCantidad()) {
                    throw new RuntimeException(
                            "Stock insuficiente para agregar nuevo producto: " + prod.getNombreComercial());
                }

                // Tratar de determinar TipoVenta (fallback a UNIDAD si no viene)
                TipoVenta tipo = nuevoReq.getTipoVenta() != null ? nuevoReq.getTipoVenta() : TipoVenta.UNIDAD;

                inventarioClient.registrarSalida(
                        prodId, nuevoReq.getCantidad(), venta.getSucursalId(), tipo,
                        "EDICION_VENTA_NUEVO_" + venta.getId());

                BigDecimal precioUnidad = prod.getPrecioVentaUnidad(); // Simplicacion, asume precio unitario flat.
                BigDecimal sub = precioUnidad.multiply(new BigDecimal(nuevoReq.getCantidad()));

                DetalleVenta nuevoDetalle = new DetalleVenta();
                nuevoDetalle.setVenta(venta);
                nuevoDetalle.setProductoId(prodId);
                nuevoDetalle.setProductoNombre(prod.getNombreComercial());
                nuevoDetalle.setCantidad(nuevoReq.getCantidad());
                nuevoDetalle.setPrecioUnitario(precioUnidad);
                nuevoDetalle.setDescuento(BigDecimal.ZERO);
                nuevoDetalle.setTipoVenta(tipo);
                nuevoDetalle.setEsVentaPorCaja(tipo == TipoVenta.CAJA);
                nuevoDetalle.setSubtotal(sub);

                detalleVentaRepository.save(nuevoDetalle);

                nuevoSubtotalGlobal = nuevoSubtotalGlobal.add(sub);
                detallesParaConservar.add(nuevoDetalle);

                logsEdicion.add("{'accion': 'NUEVO_AGREGADO', 'productoId': " + prodId + ", 'cant': "
                        + nuevoReq.getCantidad() + "}");
            }
        }

        // Reemplazar los detalles en la colección de la venta y re-simular el IVA
        // pro-rata
        venta.getDetalles().clear();
        venta.getDetalles().addAll(detallesParaConservar);

        // IVA simplificado proporcional basado en el nuevo subtotal y el histórico
        BigDecimal nuevoTotalIva = BigDecimal.ZERO;
        if (venta.getTotalIva() != null && venta.getTotalIva().compareTo(BigDecimal.ZERO) > 0
                && totalAntiguo.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factorIva = venta.getTotalIva().divide(totalAntiguo, 6, java.math.RoundingMode.HALF_UP);
            nuevoTotalIva = nuevoSubtotalGlobal.multiply(factorIva).setScale(2, java.math.RoundingMode.HALF_UP);
        }

        // 4. RECÁLCULO DE CAJA (Ajuste al Peso en Efectivo)
        BigDecimal totalFactura = nuevoSubtotalGlobal;
        BigDecimal ajusteRedondeo = BigDecimal.ZERO;

        if (com.farmacia.ms_transacciones.enums.MetodoPago.EFECTIVO.equals(venta.getMetodoPago())) {
            BigDecimal factorRedondeo = new BigDecimal("50");
            BigDecimal totalRedondeado = totalFactura.divide(factorRedondeo, 0, java.math.RoundingMode.FLOOR)
                    .multiply(factorRedondeo);
            ajusteRedondeo = totalRedondeado.subtract(totalFactura);
            totalFactura = totalRedondeado;
        }

        venta.setAjusteRedondeo(ajusteRedondeo);
        venta.setTotal(totalFactura);
        venta.setTotalIva(nuevoTotalIva);

        if (detallesParaConservar.isEmpty()) {
            venta.setEstado("ANULADA"); // Si se editaron dejando 0 items.
        }

        // 5. MOVIMIENTOS Y DELTA DE CAJA
        BigDecimal deltaFinanciero = totalFactura.subtract(totalAntiguo);

        if (deltaFinanciero.compareTo(BigDecimal.ZERO) != 0) {
            com.farmacia.ms_transacciones.model.MovimientoCaja mov = new com.farmacia.ms_transacciones.model.MovimientoCaja();
            mov.setTurno(turnoActual);
            mov.setFecha(LocalDateTime.now());
            mov.setReferencia("EDICIN VTA #" + idVenta);
            mov.setMonto(deltaFinanciero.abs());

            if (deltaFinanciero.compareTo(BigDecimal.ZERO) < 0) {
                mov.setTipo("DEVOLUCION_VENTA");
                mov.setDescripcion("Devolución por Edición de Venta. Motivo: " + solicitud.getMotivo());
                turnoActual.setTotalEgresos(
                        (turnoActual.getTotalEgresos() != null ? turnoActual.getTotalEgresos() : BigDecimal.ZERO)
                                .add(deltaFinanciero.abs()));
                turnoActual.setTotalVentasTeorico(
                        (turnoActual.getTotalVentasTeorico() != null ? turnoActual.getTotalVentasTeorico()
                                : BigDecimal.ZERO)
                                .subtract(deltaFinanciero.abs()));
            } else {
                mov.setTipo("INGRESO_VENTA");
                mov.setDescripcion("Cobro adicional por Edición de Venta. Motivo: " + solicitud.getMotivo());
                turnoActual.setTotalVentasTeorico(
                        (turnoActual.getTotalVentasTeorico() != null ? turnoActual.getTotalVentasTeorico()
                                : BigDecimal.ZERO)
                                .add(deltaFinanciero));
            }
            movimientoCajaRepository.save(mov);
            turnoCajaRepository.save(turnoActual);
        }

        // 6. REGISTRO EN BITÁCORA
        BitacoraVenta bitacora = new BitacoraVenta();
        bitacora.setVentaId(idVenta);
        bitacora.setTurnoId(turnoActual.getId());
        bitacora.setUsuarioId(String.valueOf(UserContext.getUserId()));
        bitacora.setMotivo(solicitud.getMotivo());
        bitacora.setTipoEvento(TipoEventoAuditoria.EDICION_VENTA);
        bitacora.setFechaEvento(LocalDateTime.now());

        String jsonPayload = String.format(
                "{\"deltaFinanciero\": %s, \"nuevoTotal\": %s, \"itemsModificados\": [%s]}",
                deltaFinanciero.toString(),
                totalFactura.toString(),
                String.join(",", logsEdicion).replace("'", "\""));
        bitacora.setDetallesCambiosJson(jsonPayload);
        bitacoraVentaRepository.save(bitacora);

        ventaRepository.save(venta);

        return mapToDTO(venta, nuevoTotalIva);
    }

    private BigDecimal crearDetalleDevolucionLocal(
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

        return subtotalRembolso;
    }

    @Override
    public java.util.List<com.farmacia.ms_transacciones.dto.BitacoraVentaDTO> obtenerBitacoraPorTurno(Long turnoId) {
        System.out.println("VENTA-AUDITORIA: Consultando bitácora para el turno " + turnoId);
        java.util.List<BitacoraVenta> eventos = bitacoraVentaRepository.findByTurnoIdOrderByFechaEventoDesc(turnoId);

        return eventos.stream().map(b -> {
            com.farmacia.ms_transacciones.dto.BitacoraVentaDTO dto = new com.farmacia.ms_transacciones.dto.BitacoraVentaDTO();
            dto.setId(b.getId());
            dto.setVentaId(b.getVentaId());
            dto.setTurnoId(b.getTurnoId());
            dto.setUsuarioId(b.getUsuarioId());
            dto.setTipoEvento(b.getTipoEvento() != null ? b.getTipoEvento().name() : null);
            dto.setFechaEvento(b.getFechaEvento());
            dto.setMotivo(b.getMotivo());
            dto.setDetallesCambiosJson(b.getDetallesCambiosJson());
            return dto;
        }).collect(Collectors.toList());
    }
}