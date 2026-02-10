package com.legacy.pharmacy.inventario.repository;

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

        // ✅ NUEVO MÉTODO: Busca productos donde (Suma de Lotes) <= Stock Mínimo
        // COALESCE maneja el caso donde no hay lotes (null) convirtiéndolo a 0
        @Query("SELECT p FROM Producto p " +
                        "WHERE (SELECT COALESCE(SUM(l.cantidadActual), 0) FROM Lote l " +
                        "       WHERE l.producto.id = p.id AND l.cantidadActual > 0) <= p.stockMinimo " +
                        "AND p.estado = 'ACTIVO'")
        List<Producto> findProductosBajoStock();

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
}
