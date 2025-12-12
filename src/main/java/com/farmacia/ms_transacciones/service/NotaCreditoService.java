package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.dto.response.NotaCreditoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotaCreditoService {
    NotaCreditoResponseDTO generarNotaCredito(Long devolucionId);
    NotaCreditoResponseDTO obtenerPorId(Long id);
    Page<NotaCreditoResponseDTO> listarNotasCredito(Pageable pageable);
    Page<NotaCreditoResponseDTO> buscarPorCliente(Long clienteId, Pageable pageable);
    NotaCreditoResponseDTO aplicarNotaCredito(Long id, Double montoAplicar);
}