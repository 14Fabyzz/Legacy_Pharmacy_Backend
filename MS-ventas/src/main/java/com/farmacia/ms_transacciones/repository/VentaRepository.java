package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Evita el N+1 trayendo todas las relaciones de una vez mediante LEFT JOIN
    @EntityGraph(attributePaths = { "cliente", "turno", "detalles" })
    List<Venta> findByTurnoId(Long turnoId);

    @Override
    @EntityGraph(attributePaths = { "cliente", "turno", "detalles" })
    List<Venta> findAll();
}