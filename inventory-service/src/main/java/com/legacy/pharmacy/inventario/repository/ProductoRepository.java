package com.legacy.pharmacy.inventario.repository;

import com.legacy.pharmacy.inventario.dto.StockBajoDTO;
import com.legacy.pharmacy.inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

        // Método mágico: Buscar por Código Interno
        Optional<Producto> findByCodigoInterno(String codigoInterno);

        // Método mágico: Buscar por Código de Barras
        Optional<Producto> findByCodigoBarras(String codigoBarras);

        // Método mágico: Comprobar si existe por nombre
        boolean existsByNombreComercial(String nombre);

        // Buscar por estado (ACTIVO, DESCONTINUADO)
        List<Producto> findByEstado(String estado);

        // Búsqueda parcial por nombre (LIKE %texto%) ignorando mayúsculas/minúsculas
        List<Producto> findByNombreComercialContainingIgnoreCase(String texto);

        // ✅ LEGACY — Subquery correlacionada (mantenida por compatibilidad con otros
        // usos)
        // ⚠️ NO usar en el Dashboard: evalúa la subquery fila a fila (O(N) scans)
        @Query("SELECT p FROM Producto p " +
                        "WHERE (SELECT COALESCE(SUM(l.cantidadActual), 0) FROM Lote l " +
                        "       WHERE l.producto.id = p.id AND l.cantidadActual > 0) <= p.stockMinimo " +
                        "AND p.estado = 'ACTIVO'")
        List<Producto> findProductosBajoStock();

        // ✅ OPTIMIZADO — Query nativa con LEFT JOIN + GROUP BY + HAVING
        // Calcula el stock total por producto directamente en la BD en un único
        // round-trip.
        // Devuelve Interface Projection (StockBajoDTO) con solo los 5 campos del
        // dashboard.
        // Sustituye el patrón: findProductosBajoStock() + bucle N+1 de
        // consultarStockActual()
        @Query(value = "SELECT p.id          AS id, " +
                        "       p.nombre_comercial            AS nombre, " +
                        "       COALESCE(SUM(l.cantidad_actual), 0) AS stockActual, " +
                        "       p.stock_minimo                AS stockMinimo, " +
                        "       p.imagen_url                  AS imagenUrl " +
                        "FROM productos p " +
                        "LEFT JOIN lotes l ON l.producto_id = p.id AND l.cantidad_actual > 0 " +
                        "WHERE p.estado = 'ACTIVO' " +
                        "GROUP BY p.id, p.nombre_comercial, p.stock_minimo, p.imagen_url " +
                        "HAVING COALESCE(SUM(l.cantidad_actual), 0) <= p.stock_minimo", nativeQuery = true)
        List<StockBajoDTO> findProductosBajoStockConAgregacion();

        // ✅ BÚSQUEDA UNIVERSAL (CORREGIDA CON TRIM Y NULL-SAFE)
        // Busca indiscriminadamente en las 3 columnas: Nombre, Código de Barras, Código
        // Interno
        // TRIM() asegura que los espacios en la BD no rompan la búsqueda exacta
        // COALESCE trata NULL estado como 'ACTIVO' (productos sin estado asignado)
        @Query("SELECT p FROM Producto p WHERE " +
                        "(LOWER(p.nombreComercial) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR TRIM(p.codigoBarras) = :query " +
                        "OR TRIM(p.codigoInterno) = :query) " +
                        "AND (p.estado = 'ACTIVO' OR p.estado IS NULL OR p.estado = '')")
        List<Producto> buscarUniversal(@org.springframework.data.repository.query.Param("query") String query);

        // ✅ QUERY CROSS-SERVICE para ms-reportes (Rendimiento Inventario)
        // Devuelve solo los IDs de los productos para hacer IN() en postgres
        @Query("SELECT p.id FROM Producto p WHERE " +
               "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
               "(:laboratorioId IS NULL OR p.laboratorio.id = :laboratorioId) AND " +
               "p.estado = 'ACTIVO'")
        List<Integer> findIdsByFiltros(@org.springframework.data.repository.query.Param("categoriaId") Integer categoriaId, 
                                       @org.springframework.data.repository.query.Param("laboratorioId") Integer laboratorioId);
}
