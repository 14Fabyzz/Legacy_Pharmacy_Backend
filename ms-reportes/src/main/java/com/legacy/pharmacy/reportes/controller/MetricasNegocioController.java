package com.legacy.pharmacy.reportes.controller;

import com.legacy.pharmacy.reportes.config.UserContext;
import com.legacy.pharmacy.reportes.dto.EficienciaOperativaMetricasDTO;
import com.legacy.pharmacy.reportes.dto.GestionInventarioMetricasDTO;
import com.legacy.pharmacy.reportes.dto.ResumenInteligenteResponseDTO;
import com.legacy.pharmacy.reportes.dto.VentasClientesMetricasDTO;
import com.legacy.pharmacy.reportes.enums.Periodicidad;
import com.legacy.pharmacy.reportes.exception.BusinessException;
import com.legacy.pharmacy.reportes.service.ResumenInteligenteService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorInventarioService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorOperativoService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorVentasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:80"})
@RestController
@RequestMapping("/api/v1/metricas")
public class MetricasNegocioController {

        private static final Logger log = LoggerFactory.getLogger(MetricasNegocioController.class);

        private final ResumenInteligenteService resumenInteligenteService;
        private final CalculadorInventarioService calculadorInventarioService;
        private final CalculadorVentasService calculadorVentasService;
        private final CalculadorOperativoService calculadorOperativoService;

        public MetricasNegocioController(ResumenInteligenteService resumenInteligenteService,
                                         CalculadorInventarioService calculadorInventarioService,
                                         CalculadorVentasService calculadorVentasService,
                                         CalculadorOperativoService calculadorOperativoService) {
                this.resumenInteligenteService = resumenInteligenteService;
                this.calculadorInventarioService = calculadorInventarioService;
                this.calculadorVentasService = calculadorVentasService;
                this.calculadorOperativoService = calculadorOperativoService;
        }

        @GetMapping("/inventario")
        public ResponseEntity<GestionInventarioMetricasDTO> obtenerMetricasInventario(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                        @RequestParam(required = false) Integer sucursalId) {
                if (!UserContext.isAdmin()) {
                        throw new BusinessException("Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden consultar estas métricas");
                }
                GestionInventarioMetricasDTO metricas = calculadorInventarioService.calcularPulso(fechaInicio, fechaFin, sucursalId);
                return ResponseEntity.ok(metricas);
        }

        @GetMapping("/ventas-clientes")
        public ResponseEntity<VentasClientesMetricasDTO> obtenerMetricasVentasYClientes(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                        @RequestParam(required = false) Integer sucursalId) {
                if (!UserContext.isAdmin()) {
                        throw new BusinessException("Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden consultar estas métricas");
                }
                VentasClientesMetricasDTO metricas = calculadorVentasService.calcularMotor(fechaInicio, fechaFin, sucursalId);
                return ResponseEntity.ok(metricas);
        }

        @GetMapping("/eficiencia-operativa")
        public ResponseEntity<EficienciaOperativaMetricasDTO> obtenerMetricasEficienciaOperativa(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                        @RequestParam(required = false) Integer sucursalId) {
                if (!UserContext.isAdmin()) {
                        throw new BusinessException("Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden consultar estas métricas");
                }
                EficienciaOperativaMetricasDTO metricas = calculadorOperativoService.calcularSalud(fechaInicio, fechaFin, sucursalId);
                return ResponseEntity.ok(metricas);
        }

        @GetMapping("/resumen-ia")
        public ResponseEntity<ResumenInteligenteResponseDTO> generarResumenInteligente(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                        @RequestParam Periodicidad periodicidad,
                        @RequestParam(required = false) Integer sucursalId) {

                if (!UserContext.isAdmin()) {
                        throw new BusinessException(
                                        "Acceso denegado: solo usuarios con rol ADMINISTRADOR pueden generar resúmenes con IA");
                }

                log.info("Solicitud de resumen inteligente de métricas: {} a {}, periodicidad={}, sucursal={}, usuario={}",
                                fechaInicio, fechaFin, periodicidad, sucursalId, UserContext.getUsername());

                ResumenInteligenteResponseDTO resumen = resumenInteligenteService.generarResumen(
                                fechaInicio, fechaFin, sucursalId);

                return ResponseEntity.ok(resumen);
        }
}
