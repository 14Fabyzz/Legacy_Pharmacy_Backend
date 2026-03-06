package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.model.BitacoraVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BitacoraVentaRepository extends JpaRepository<BitacoraVenta, Long> {

    List<BitacoraVenta> findByTurnoIdOrderByFechaEventoDesc(Long turnoId);

    List<BitacoraVenta> findByVentaIdOrderByFechaEventoDesc(Long ventaId);

}
