package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.dto.AperturaTurnoDTO;
import com.farmacia.ms_transacciones.dto.CierreTurnoDTO;
import com.farmacia.ms_transacciones.entity.TurnoCaja;
import com.farmacia.ms_transacciones.entity.Venta;
import com.farmacia.ms_transacciones.repository.TurnoCajaRepository;
import com.farmacia.ms_transacciones.repository.VentasRepository;
import com.farmacia.ms_transacciones.service.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CajaServiceImpl implements CajaService {

    @Autowired
    private TurnoCajaRepository turnoCajaRepository;

    @Autowired
    private VentasRepository ventasRepository;

    @Override
    @Transactional
    public TurnoCaja abrirTurno(AperturaTurnoDTO dto) {
        // 1. Validar que no tenga turno abierto
        Optional<TurnoCaja> turnoActivo = turnoCajaRepository.findByUsuarioIdAndEstado(dto.getUsuarioId(), "abierto");
        if (turnoActivo.isPresent()) {
            throw new RuntimeException("El usuario ya tiene un turno activo. Debe cerrarlo antes de abrir uno nuevo.");
        }

        // 2. Crear el nuevo turno
        TurnoCaja nuevoTurno = new TurnoCaja();
        nuevoTurno.setUsuarioId(dto.getUsuarioId());
        nuevoTurno.setSucursalId(dto.getSucursalId());
        nuevoTurno.setSaldoInicial(dto.getSaldoInicial());
        nuevoTurno.setFechaApertura(LocalDateTime.now());
        nuevoTurno.setEstado("abierto");

        // Inicializar contadores en cero
        nuevoTurno.setTotalVentasTeorico(BigDecimal.ZERO);
        nuevoTurno.setTotalEfectivoTeorico(dto.getSaldoInicial()); // Empezamos con la base
        nuevoTurno.setNumeroVentas(0);

        return turnoCajaRepository.save(nuevoTurno);
    }

    @Override
    @Transactional
    public TurnoCaja cerrarTurno(String usuarioId, CierreTurnoDTO dto) {
        // 1. Buscar turno abierto
        TurnoCaja turno = turnoCajaRepository.findByUsuarioIdAndEstado(usuarioId, "abierto")
                .orElseThrow(() -> new RuntimeException("No hay turno abierto para cerrar."));

        // 2. Calcular totales reales desde las VENTAS
        List<Venta> ventasDelTurno = ventasRepository.findByTurnoId(turno.getId());

        BigDecimal sumaVentas = BigDecimal.ZERO;
        BigDecimal sumaEfectivo = BigDecimal.ZERO;
        BigDecimal sumaTarjetas = BigDecimal.ZERO;

        for (Venta v : ventasDelTurno) {
            if ("completada".equals(v.getEstado())) { // Solo ventas válidas
                sumaVentas = sumaVentas.add(v.getTotal());

                if ("efectivo".equalsIgnoreCase(v.getFormaPago())) {
                    sumaEfectivo = sumaEfectivo.add(v.getTotal());
                } else {
                    sumaTarjetas = sumaTarjetas.add(v.getTotal());
                }
            }
        }

        // 3. Actualizar datos del turno
        turno.setFechaCierre(LocalDateTime.now());
        turno.setNumeroVentas(ventasDelTurno.size());
        turno.setTotalVentasTeorico(sumaVentas);
        turno.setTotalTarjetas(sumaTarjetas);

        // Efectivo Teórico = Saldo Inicial + Ventas Efectivo (Falta sumar ingresos/egresos manuales si los tuvieras)
        BigDecimal efectivoTeoricoTotal = turno.getSaldoInicial().add(sumaEfectivo);
        turno.setTotalEfectivoTeorico(efectivoTeoricoTotal);

        // 4. Comparar con lo que contó el cajero (Arqueo)
        turno.setTotalEfectivoReal(dto.getEfectivoReal());
        BigDecimal diferencia = dto.getEfectivoReal().subtract(efectivoTeoricoTotal);
        turno.setDiferencia(diferencia);
        turno.setObservacionesCierre(dto.getObservaciones());

        // 5. Definir estado final (Cuadrado o Descuadrado)
        if (diferencia.compareTo(BigDecimal.ZERO) == 0) {
            turno.setEstado("cuadrado");
        } else {
            turno.setEstado("descuadrado");
        }

        return turnoCajaRepository.save(turno);
    }

    @Override
    public Optional<TurnoCaja> obtenerTurnoActivo(String usuarioId) {
        return turnoCajaRepository.findByUsuarioIdAndEstado(usuarioId, "abierto");
    }
}
