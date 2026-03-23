package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.model.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    List<Devolucion> findByVentaId(Long ventaId);
}
