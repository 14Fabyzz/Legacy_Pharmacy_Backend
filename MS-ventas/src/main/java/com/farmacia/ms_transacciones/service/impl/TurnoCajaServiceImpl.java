package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.config.UserContext;
import com.farmacia.ms_transacciones.dto.AperturaCajaDTO;
import com.farmacia.ms_transacciones.dto.CierreCajaDTO;
import com.farmacia.ms_transacciones.model.TurnoCaja;
import com.farmacia.ms_transacciones.repository.TurnoCajaRepository;
import com.farmacia.ms_transacciones.repository.VentaRepository;
import com.farmacia.ms_transacciones.service.TurnoCajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.farmacia.ms_transacciones.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoCajaServiceImpl implements TurnoCajaService {

        @Autowired
        private TurnoCajaRepository turnoCajaRepository;

        @Autowired
        private VentaRepository ventaRepository; // Necesario para sumar las ventas al cerrar

        @Autowired
        private com.farmacia.ms_transacciones.repository.MovimientoCajaRepository movimientoCajaRepository;

        @Override
        public TurnoCaja abrirCaja(AperturaCajaDTO datos) {
                String usuarioId = String.valueOf(UserContext.getUserId());

                // 1. Validar que no tenga caja abierta
                Optional<TurnoCaja> turnoExistente = turnoCajaRepository.findByUsuarioIdAndEstado(usuarioId, "ABIERTO");
                if (turnoExistente.isPresent()) {
                        throw new RuntimeException("El usuario ya tiene un turno abierto. Debe cerrarlo primero.");
                }

                // 2. Crear nuevo turno
                TurnoCaja turno = new TurnoCaja();
                turno.setUsuarioId(usuarioId);
                turno.setSucursalId(datos.getSucursalId());
                turno.setFechaApertura(LocalDateTime.now());
                turno.setSaldoInicial(datos.getSaldoInicial());
                turno.setEstado("ABIERTO");

                // Inicializar valores en 0
                turno.setTotalVentasTeorico(BigDecimal.ZERO);
                turno.setTotalEfectivoReal(BigDecimal.ZERO);
                turno.setDiferencia(BigDecimal.ZERO);

                return turnoCajaRepository.save(turno);
        }

        @Override
        @Transactional
        public TurnoCaja cerrarCaja(CierreCajaDTO datos) {
                // 1. Obtener turno abierto
                TurnoCaja turno = obtenerTurnoAbiertoActual();

                // 2. Calcular Total de Ventas reales (Ingresos - Egresos/Devoluciones)
                List<com.farmacia.ms_transacciones.model.MovimientoCaja> movimientos = movimientoCajaRepository
                                .findByTurnoId(turno.getId());

                BigDecimal totalIngresos = movimientos.stream()
                                .filter(m -> m.getTipo() != null
                                                && (m.getTipo().startsWith("INGRESO") || "VENTA".equals(m.getTipo())))
                                .map(com.farmacia.ms_transacciones.model.MovimientoCaja::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalEgresos = movimientos.stream()
                                .filter(m -> m.getTipo() != null
                                                && (m.getTipo().startsWith("EGRESO")
                                                                || m.getTipo().startsWith("DEVOLUCION")))
                                .map(com.farmacia.ms_transacciones.model.MovimientoCaja::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalVentasNetas = totalIngresos.subtract(totalEgresos);

                // 3. Calcular diferencia
                BigDecimal esperado = turno.getSaldoInicial().add(totalVentasNetas);
                BigDecimal diferencia = datos.getTotalEfectivoReal().subtract(esperado);

                // 4. Actualizar datos de cierre en el objeto turno
                turno.setDiferencia(diferencia);
                turno.setTotalVentasTeorico(totalVentasNetas);
                turno.setTotalEgresos(totalEgresos);
                turno.setTotalEfectivoReal(datos.getTotalEfectivoReal());
                turno.setObservacionesCierre(datos.getObservaciones());

                // --- VALIDACIÓN DE JUSTIFICACIÓN ---
                // Si la diferencia NO es cero (positiva o negativa)
                if (diferencia.compareTo(BigDecimal.ZERO) != 0) {
                        // Y las observaciones están vacías o nulas
                        if (datos.getObservaciones() == null || datos.getObservaciones().trim().isEmpty()) {
                                throw new BusinessException("ERROR AL CERRAR: Existe un descuadre de " + diferencia
                                                + ". Debe ingresar una observación justificando el faltante o sobrante.");
                        }
                }
                // -----------------------------------

                turno.setFechaCierre(LocalDateTime.now());
                turno.setEstado("CERRADO");

                return turnoCajaRepository.save(turno);
        }

        @Override
        public TurnoCaja obtenerTurnoAbiertoActual() {
                String usuarioId = String.valueOf(UserContext.getUserId());
                TurnoCaja turno = turnoCajaRepository.findByUsuarioIdAndEstado(usuarioId, "ABIERTO")
                                .orElseThrow(() -> new RuntimeException("No hay un turno abierto para este usuario."));
                return recalcularTotales(turno);
        }

        @Override
        public TurnoCaja obtenerTurnoAbiertoGlobal() {
                // Busca el turno activo a nivel general, devolviendo el más reciente en estado
                // ABIERTO
                TurnoCaja turno = turnoCajaRepository.findFirstByEstadoOrderByFechaAperturaDesc("ABIERTO")
                                .orElseThrow(() -> new RuntimeException(
                                                "Actualmente no hay ninguna caja abierta en la farmacia."));
                return recalcularTotales(turno);
        }

        private TurnoCaja recalcularTotales(TurnoCaja turno) {
                // Recalcular saldo dinámicamente en cada consulta para reflejar devoluciones
                List<com.farmacia.ms_transacciones.model.MovimientoCaja> movimientos = movimientoCajaRepository
                                .findByTurnoId(turno.getId());

                BigDecimal totalIngresos = movimientos.stream()
                                .filter(m -> m.getTipo() != null
                                                && (m.getTipo().startsWith("INGRESO") || "VENTA".equals(m.getTipo())))
                                .map(com.farmacia.ms_transacciones.model.MovimientoCaja::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalEgresos = movimientos.stream()
                                .filter(m -> m.getTipo() != null
                                                && (m.getTipo().startsWith("EGRESO")
                                                                || m.getTipo().startsWith("DEVOLUCION")))
                                .map(com.farmacia.ms_transacciones.model.MovimientoCaja::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                turno.setTotalVentasTeorico(totalIngresos.subtract(totalEgresos));
                turno.setTotalEgresos(totalEgresos);

                return turno;
        }
}