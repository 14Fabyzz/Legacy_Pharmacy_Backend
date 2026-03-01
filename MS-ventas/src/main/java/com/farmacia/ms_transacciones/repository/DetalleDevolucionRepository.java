package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.model.DetalleDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleDevolucionRepository extends JpaRepository<DetalleDevolucion, Long> {

    List<DetalleDevolucion> findByDevolucionId(Long devolucionId);

    List<DetalleDevolucion> findByDetalleVentaId(Long detalleVentaId);

    @Query("SELECT SUM(d.cantidadDevuelta) FROM DetalleDevolucion d WHERE d.detalleVenta.id = :detalleVentaId")
    Integer sumCantidadDevueltaByDetalleVentaId(@Param("detalleVentaId") Long detalleVentaId);
}
