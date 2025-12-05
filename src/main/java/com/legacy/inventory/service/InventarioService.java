package com.legacy.inventory.service;

import com.legacy.inventory.dto.EntradaMercanciaDTO;
import com.legacy.inventory.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class InventarioService {

    @Autowired
    private LoteRepository loteRepository;

    @Transactional
    public Map<String, Object> registrarEntrada(EntradaMercanciaDTO entrada) {

        // Llamamos al repositorio y retornamos lo que responda la base de datos
        return loteRepository.registrarEntrada(
                entrada.getProductoId(),
                entrada.getNumeroLote(),
                entrada.getCantidad(),
                entrada.getCostoCompra(),
                entrada.getFechaVencimiento(),
                entrada.getUsuarioResponsable() != null ? entrada.getUsuarioResponsable() : "SISTEMA",
                entrada.getSucursalId(),
                entrada.getObservaciones()
        );
    }

    // --- NUEVO MÉTODO DE SALIDA ---
    @Transactional
    public List<Map<String, Object>> registrarSalida(com.legacy.inventory.dto.SalidaMercanciaDTO salida) {

        // Llamamos al SP. Este nos devolverá la lista de lotes afectados.
        return loteRepository.registrarSalida(
                salida.getProductoId(),
                salida.getCantidad(),
                "VENDEDOR_APP", // Aquí podrías poner el usuario logueado
                salida.getSucursalId(),
                salida.getVentaId(),
                salida.getObservaciones()
        );
    }
}