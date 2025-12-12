package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.entity.TurnoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TurnoCajaRepository extends JpaRepository<TurnoCaja, Long> {

    // Buscar si un usuario tiene un turno abierto (Para evitar abrir dos veces - HU-09.1)
    Optional<TurnoCaja> findByUsuarioIdAndEstado(String usuarioId, String estado);
}