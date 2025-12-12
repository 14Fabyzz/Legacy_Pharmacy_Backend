package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.entity.NotaCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotaCreditoRepository extends JpaRepository<NotaCredito, Long> {
    List<NotaCredito> findByClienteId(Long clienteId);
    NotaCredito findByNumeroNota(String numeroNota);
}

