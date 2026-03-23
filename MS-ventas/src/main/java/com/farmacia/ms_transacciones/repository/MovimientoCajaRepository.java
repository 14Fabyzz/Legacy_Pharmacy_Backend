package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.model.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByTurnoId(Long turnoId);
}
