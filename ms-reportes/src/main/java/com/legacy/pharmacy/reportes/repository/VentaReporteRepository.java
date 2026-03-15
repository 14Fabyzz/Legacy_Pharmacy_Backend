package com.legacy.pharmacy.reportes.repository;

import com.legacy.pharmacy.reportes.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de consultas de ventas para reportes (solo lectura).
 * Usa queries nativas PostgreSQL para agregaciones con DATE_TRUNC.
 */
@Repository
public interface VentaReporteRepository extends JpaRepository<Venta, Long> {

}
