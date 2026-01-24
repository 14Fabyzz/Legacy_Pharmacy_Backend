package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.dto.AuditoriaDTO;
import com.legacy.pharmacy.inventario.entity.Lote;
import com.legacy.pharmacy.inventario.entity.Movimiento;
import com.legacy.pharmacy.inventario.entity.TipoMovimiento;
import com.legacy.pharmacy.inventario.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    // Método para consultar el reporte (El que usará el Controlador)
    public List<AuditoriaDTO> obtenerHistorialCompleto() {
        return movimientoRepository.obtenerAuditoriaCompleta();
    }

    // Método utilitario para registrar movimientos desde otros servicios (Ventas, Compras)
    @Transactional
    public void registrarMovimiento(Lote lote, TipoMovimiento tipo, Integer cantidad, String responsable, String observacion) {
        Movimiento movimiento = Movimiento.builder()
                .lote(lote)
                .tipoMovimiento(tipo)
                .cantidad(cantidad) // Asegúrate de mandar negativo si es salida
                .usuarioResponsable(responsable)
                .observacion(observacion)
                .build();

        movimientoRepository.save(movimiento);
    }
}