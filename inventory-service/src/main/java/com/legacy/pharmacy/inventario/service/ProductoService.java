package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.dto.ProductoDTO;
import com.legacy.pharmacy.inventario.entity.*;
import com.legacy.pharmacy.inventario.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private LaboratorioRepository laboratorioRepository;
    @Autowired
    private PrincipioActivoRepository principioActivoRepository;
    @Autowired
    private LoteRepository loteRepository;

    // --- PRODUCTOS ---

    public List<Producto> listarProductos(String estado) {
        if (estado != null) {
            return productoRepository.findByEstado(estado);
        }
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public Producto buscarPorCodigoInterno(String codigo) {
        return productoRepository.findByCodigoInterno(codigo)
                .orElseThrow(() -> new RuntimeException("Código interno no existe"));
    }

    public Producto buscarPorCodigoBarras(String codigo) {
        return productoRepository.findByCodigoBarras(codigo)
                .orElseThrow(() -> new RuntimeException("Código de barras no existe"));
    }

    public List<Producto> buscarPorNombre(String texto) {
        return productoRepository.findByNombreComercialContainingIgnoreCase(texto);
    }

    public Producto guardarProducto(ProductoDTO dto) {
        Producto p = new Producto();

        // Mapeo de campos básicos
        p.setCodigoInterno(dto.getCodigoInterno());
        p.setCodigoBarras(dto.getCodigoBarras());
        p.setNombreComercial(dto.getNombreComercial());
        p.setConcentracion(dto.getConcentracion());
        p.setPresentacion(dto.getPresentacion());
        p.setRegistroInvima(dto.getRegistroInvima());
        p.setEstado("ACTIVO");

        // Mapeo de campos de configuración
        p.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 10);
        p.setEsControlado(dto.getEsControlado() != null ? dto.getEsControlado() : false);
        p.setRefrigerado(dto.getRefrigerado() != null ? dto.getRefrigerado() : false);

        // Tipo de producto
        if (dto.getTipo() != null) {
            p.setTipo(com.legacy.pharmacy.inventario.entity.TipoProducto.valueOf(dto.getTipo()));
        } else {
            p.setTipo(com.legacy.pharmacy.inventario.entity.TipoProducto.TANGIBLE);
        }

        // Fraccionamiento
        p.setEsFraccionable(dto.getEsFraccionable() != null ? dto.getEsFraccionable() : false);
        p.setUnidadesPorCaja(dto.getUnidadesPorCaja() != null ? dto.getUnidadesPorCaja() : 1);
        p.setUnidadesPorBlister(dto.getUnidadesPorBlister());

        // === MAPEO DE CAMPOS DE ENTRADA PARA PRECIOS ===
        p.setPrecioCompraReferencia(dto.getPrecioCompraReferencia());
        p.setPorcentajeGanancia(dto.getPorcentajeGanancia());
        p.setIvaPorcentaje(dto.getIvaPorcentaje());

        // Relaciones
        p.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElseThrow());
        p.setLaboratorio(laboratorioRepository.findById(dto.getLaboratorioId()).orElseThrow());
        if (dto.getPrincipioActivoId() != null) {
            p.setPrincipioActivo(principioActivoRepository.findById(dto.getPrincipioActivoId()).orElse(null));
        }

        // === CALCULAR PRECIOS AUTOMÁTICAMENTE ===
        p.recalcularPrecios();

        return productoRepository.save(p);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Integer id, ProductoDTO dto) {
        Producto p = buscarPorId(id);

        // Actualizamos campos clave
        if (dto.getNombreComercial() != null)
            p.setNombreComercial(dto.getNombreComercial());
        if (dto.getPrecioVentaBase() != null)
            p.setPrecioVentaBase(dto.getPrecioVentaBase());
        if (dto.getCodigoInterno() != null)
            p.setCodigoInterno(dto.getCodigoInterno());
        if (dto.getCodigoBarras() != null)
            p.setCodigoBarras(dto.getCodigoBarras());
        if (dto.getConcentracion() != null)
            p.setConcentracion(dto.getConcentracion());
        if (dto.getPresentacion() != null)
            p.setPresentacion(dto.getPresentacion());
        if (dto.getRegistroInvima() != null)
            p.setRegistroInvima(dto.getRegistroInvima());
        if (dto.getStockMinimo() != null)
            p.setStockMinimo(dto.getStockMinimo());
        if (dto.getEsControlado() != null)
            p.setEsControlado(dto.getEsControlado());
        if (dto.getRefrigerado() != null)
            p.setRefrigerado(dto.getRefrigerado());

        // Actualizar tipo si viene en el DTO
        if (dto.getTipo() != null) {
            p.setTipo(com.legacy.pharmacy.inventario.entity.TipoProducto.valueOf(dto.getTipo()));
        }

        // Actualizamos campos de fraccionamiento
        if (dto.getEsFraccionable() != null)
            p.setEsFraccionable(dto.getEsFraccionable());
        if (dto.getUnidadesPorCaja() != null)
            p.setUnidadesPorCaja(dto.getUnidadesPorCaja());
        if (dto.getUnidadesPorBlister() != null)
            p.setUnidadesPorBlister(dto.getUnidadesPorBlister());

        // === ACTUALIZAR CAMPOS DE ENTRADA PARA PRECIOS ===
        if (dto.getPrecioCompraReferencia() != null)
            p.setPrecioCompraReferencia(dto.getPrecioCompraReferencia());
        if (dto.getPorcentajeGanancia() != null)
            p.setPorcentajeGanancia(dto.getPorcentajeGanancia());
        if (dto.getIvaPorcentaje() != null)
            p.setIvaPorcentaje(dto.getIvaPorcentaje());

        // Actualizamos relaciones si vienen en el DTO
        if (dto.getCategoriaId() != null) {
            p.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElseThrow());
        }
        if (dto.getLaboratorioId() != null) {
            p.setLaboratorio(laboratorioRepository.findById(dto.getLaboratorioId()).orElseThrow());
        }
        if (dto.getPrincipioActivoId() != null) {
            p.setPrincipioActivo(principioActivoRepository.findById(dto.getPrincipioActivoId()).orElse(null));
        }

        // === RECALCULAR PRECIOS AUTOMÁTICAMENTE ===
        p.recalcularPrecios();

        return productoRepository.save(p);
    }

    public void cambiarEstado(Integer id, String nuevoEstado) {
        Producto p = buscarPorId(id);
        p.setEstado(nuevoEstado);
        productoRepository.save(p);
    }

    // --- LOTES ---

    public List<Lote> buscarLotesPorProducto(Integer productoId) {
        return loteRepository.findByProductoId(productoId);
    }

    public List<Lote> buscarLotesVencidos() {
        return loteRepository.findByFechaVencimientoBeforeAndCantidadActualGreaterThan(LocalDate.now(), 0);
    }

    public List<Lote> buscarLotesProximosVencer(int dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        return loteRepository.findByFechaVencimientoBetweenAndCantidadActualGreaterThan(hoy, limite, 0);
    }

    public Lote buscarLotePorId(Integer id) {
        return loteRepository.findById(id).orElseThrow(() -> new RuntimeException("Lote no encontrado"));
    }

    public List<Lote> obtenerLotesDisponiblesParaVenta(Integer productoId) {
        // Usamos 0 para traer cualquier lote que tenga al menos 1 unidad (Stock Real)
        // El repositorio ya se encarga de ordenar por fecha (FEFO)
        return loteRepository.findByProductoIdAndCantidadActualGreaterThanOrderByFechaVencimientoAsc(productoId, 0);
    }

    /**
     * Obtiene un producto con sus lotes disponibles y detalles financieros.
     * Utilizado para el modal de vista rápida en el Frontend.
     * 
     * @param productoId ID del producto
     * @return ProductoConLotesDTO con información financiera y lista de lotes
     * @throws ResourceNotFoundException si el producto no existe
     */
    public com.legacy.pharmacy.inventario.dto.ProductoConLotesDTO obtenerProductoConLotesDisponibles(
            Integer productoId) {
        // 1. Buscar el producto (lanza excepción si no existe)
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new com.legacy.pharmacy.inventario.exception.ResourceNotFoundException(
                        "Producto con ID " + productoId + " no encontrado"));

        // 2. Buscar lotes disponibles
        List<Lote> lotes = obtenerLotesDisponiblesParaVenta(productoId);

        // 3. Mapear lotes a LoteDTO
        List<com.legacy.pharmacy.inventario.dto.LoteDTO> loteDTOs = lotes.stream()
                .map(lote -> com.legacy.pharmacy.inventario.dto.LoteDTO.builder()
                        .id(lote.getId())
                        .numeroLote(lote.getNumeroLote())
                        .fechaVencimiento(lote.getFechaVencimiento())
                        .cantidadActual(lote.getCantidadActual())
                        .costoCompra(lote.getCostoCompra())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        // 4. Calcular stock total
        Integer stockTotal = lotes.stream()
                .mapToInt(Lote::getCantidadActual)
                .sum();

        // 5. Mapear producto a DetalleProductoDTO
        com.legacy.pharmacy.inventario.dto.DetalleProductoDTO detalleProducto = com.legacy.pharmacy.inventario.dto.DetalleProductoDTO
                .builder()
                .nombreComercial(producto.getNombreComercial())
                .codigoInterno(producto.getCodigoInterno())
                .precioCompraReferencia(producto.getPrecioCompraReferencia())
                .porcentajeGanancia(producto.getPorcentajeGanancia())
                .ivaPorcentaje(producto.getIvaPorcentaje())
                .precioVentaBase(producto.getPrecioVentaBase())
                .precioVentaTotal(producto.getPrecioVentaTotal())
                .precioVentaUnidad(producto.getPrecioVentaUnidad())
                .precioVentaBlister(producto.getPrecioVentaBlister())
                .stockTotal(stockTotal)
                .imagenUrl(producto.getImagenUrl()) // ✅ Mapeo de Imagen
                .build();

        // 6. Construir y retornar el DTO completo
        return com.legacy.pharmacy.inventario.dto.ProductoConLotesDTO.builder()
                .detalleProducto(detalleProducto)
                .lotes(loteDTOs)
                .build();
    }

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    // --- INTEGRACIÓN KIOSCO (BÚSQUEDA UNIVERSAL) ---
    public java.util.List<com.legacy.pharmacy.inventario.dto.StockDTO> buscarProductosUniversal(String query) {
        log.debug("SERVICE: Iniciando búsqueda universal con query: [{}]", query);

        // 1. Limpieza básica
        String queryTrimmed = query.trim();
        log.debug("SERVICE: Query después de trim: [{}]", queryTrimmed);

        // 2. Búsqueda Única Polimórfica (JPQL)
        // Ya no validamos si es número o texto, la base de datos decide.
        java.util.List<Producto> productos = productoRepository.buscarUniversal(queryTrimmed);
        log.info("SERVICE: Productos encontrados en BD: {}", productos.size());

        if (productos.isEmpty()) {
            log.warn("SERVICE: No se encontraron productos para query: [{}]", queryTrimmed);
            return java.util.Collections.emptyList();
        }

        // Log de productos encontrados
        productos.forEach(p -> log.debug("  - Producto: {} | Código Barras: [{}] | Código Interno: [{}]",
                p.getNombreComercial(), p.getCodigoBarras(), p.getCodigoInterno()));

        // 4. Mapeo a StockDTO (Reutilizando lógica de InventarioService si es posible,
        // o manual)
        // Como InventoryService depende de ProductoService (probable ciclo), mapeamos
        // aquí manualmente.

        return productos.stream().map(p -> {
            // Consultamos stock RÁPIDO
            Integer disponible = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(cantidad_actual), 0) FROM lotes WHERE producto_id = ? AND cantidad_actual > 0 AND fecha_vencimiento > CURDATE()",
                    Integer.class, p.getId());

            String estadoStock = (disponible == null || disponible == 0) ? "SIN_STOCK"
                    : (disponible <= p.getStockMinimo() ? "STOCK_BAJO" : "STOCK_OK");

            com.legacy.pharmacy.inventario.dto.StockDTO dto = new com.legacy.pharmacy.inventario.dto.StockDTO();
            dto.setProductoId(p.getId());
            dto.setNombreProducto(p.getNombreComercial());
            dto.setTipo(p.getTipo().name());
            dto.setPrecioVentaBase(p.getPrecioVentaBase());
            dto.setPrecioVentaUnidad(p.getPrecioVentaUnidad());
            dto.setPrecioVentaBlister(p.getPrecioVentaBlister());
            dto.setEsFraccionable(p.getEsFraccionable());
            dto.setUnidadesPorCaja(p.getUnidadesPorCaja());
            dto.setUnidadesPorBlister(p.getUnidadesPorBlister());
            dto.setEsControlado(p.getEsControlado());
            dto.setCantidadDisponible(disponible != null ? disponible : 0);
            dto.setCantidadMinima(p.getStockMinimo());
            dto.setEstado(estadoStock);
            dto.setDisponibleParaVenta(disponible != null && disponible > 0 && "ACTIVO".equals(p.getEstado()));
            dto.setImagenUrl(p.getImagenUrl()); // ✅ Mapeo de Imagen

            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }
}