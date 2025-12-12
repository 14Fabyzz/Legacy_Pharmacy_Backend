package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    List<Devolucion> findByVentaId(Long ventaId);
    Devolucion findByNumeroDevolucion(String numeroDevolucion);
}