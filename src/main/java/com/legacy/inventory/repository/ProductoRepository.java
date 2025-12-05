package com.legacy.inventory.repository;

import com.legacy.inventory.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
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
}