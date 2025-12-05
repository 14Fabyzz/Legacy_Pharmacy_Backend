package com.legacy.inventory.service;

import com.legacy.inventory.dto.ProductoDTO;
import com.legacy.inventory.entity.*;
import com.legacy.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductoService {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private LaboratorioRepository laboratorioRepository;
    @Autowired private PrincipioActivoRepository principioActivoRepository;
    @Autowired private LoteRepository loteRepository;

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
        // Mapeo manual (O usar MapStruct en el futuro)
        p.setCodigoInterno(dto.getCodigoInterno());
        p.setCodigoBarras(dto.getCodigoBarras());
        p.setNombreComercial(dto.getNombreComercial());
        p.setPrecioVentaBase(dto.getPrecioVentaBase());
        p.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 10);
        p.setEsControlado(dto.getEsControlado() != null ? dto.getEsControlado() : false);
        p.setRefrigerado(dto.getRefrigerado() != null ? dto.getRefrigerado() : false);
        p.setConcentracion(dto.getConcentracion());
        p.setPresentacion(dto.getPresentacion());
        p.setRegistroInvima(dto.getRegistroInvima());
        p.setEstado("ACTIVO");

        // Relaciones
        p.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElseThrow());
        p.setLaboratorio(laboratorioRepository.findById(dto.getLaboratorioId()).orElseThrow());
        if(dto.getPrincipioActivoId() != null) {
            p.setPrincipioActivo(principioActivoRepository.findById(dto.getPrincipioActivoId()).orElse(null));
        }

        return productoRepository.save(p);
    }

    public Producto actualizarProducto(Integer id, ProductoDTO dto) {
        Producto p = buscarPorId(id);
        // Actualizamos campos clave
        p.setNombreComercial(dto.getNombreComercial());
        p.setPrecioVentaBase(dto.getPrecioVentaBase());
        // ... (Agregar el resto de campos si es necesario)
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
}