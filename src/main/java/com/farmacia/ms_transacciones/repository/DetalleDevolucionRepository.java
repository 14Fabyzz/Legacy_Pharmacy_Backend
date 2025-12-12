package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.entity.DetalleDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleDevolucionRepository extends JpaRepository<DetalleDevolucion, Long> {
    List<DetalleDevolucion> findByDevolucionId(Long devolucionId);
    List<DetalleDevolucion> findByDetalleVentaId(Long detalleVentaId);
    List<DetalleDevolucion> findByEstado(String estado);
}
