package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.dto.AperturaTurnoDTO;
import com.farmacia.ms_transacciones.dto.CierreTurnoDTO;
import com.farmacia.ms_transacciones.entity.TurnoCaja;

import java.util.Optional;

public interface CajaService {
    TurnoCaja abrirTurno(AperturaTurnoDTO aperturaDTO);
    TurnoCaja cerrarTurno(String usuarioId, CierreTurnoDTO cierreDTO);
    Optional<TurnoCaja> obtenerTurnoActivo(String usuarioId);
}
