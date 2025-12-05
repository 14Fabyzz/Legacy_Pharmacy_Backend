package com.legacy.inventory.repository;

import com.legacy.inventory.entity.ProductoCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoCardRepository extends JpaRepository<ProductoCard, Integer> {

    // Método para el buscador del dashboard (Búsqueda rápida por nombre o código)
    // SELECT * FROM v_stock_productos WHERE nombre_comercial LIKE %x% OR codigo_interno LIKE %x%
    List<ProductoCard> findByNombreContainingIgnoreCaseOrCodigoInternoContainingIgnoreCaseOrCodigoBarrasContainingIgnoreCase(String nombre, String codigoInterno, String codigoBarras);
}