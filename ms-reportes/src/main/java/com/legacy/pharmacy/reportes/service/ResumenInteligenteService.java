package com.legacy.pharmacy.reportes.service;

import com.legacy.pharmacy.reportes.dto.EficienciaOperativaMetricasDTO;
import com.legacy.pharmacy.reportes.dto.GestionInventarioMetricasDTO;
import com.legacy.pharmacy.reportes.dto.ResumenInteligenteResponseDTO;
import com.legacy.pharmacy.reportes.dto.VentasClientesMetricasDTO;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorInventarioService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorOperativoService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorVentasService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ResumenInteligenteService {

    private final CalculadorInventarioService calculadorInventarioService;
    private final CalculadorVentasService calculadorVentasService;
    private final CalculadorOperativoService calculadorOperativoService;
    private final GeminiClientService geminiClientService;

    public ResumenInteligenteService(CalculadorInventarioService calculadorInventarioService,
                                     CalculadorVentasService calculadorVentasService,
                                     CalculadorOperativoService calculadorOperativoService,
                                     GeminiClientService geminiClientService) {
        this.calculadorInventarioService = calculadorInventarioService;
        this.calculadorVentasService = calculadorVentasService;
        this.calculadorOperativoService = calculadorOperativoService;
        this.geminiClientService = geminiClientService;
    }

    public ResumenInteligenteResponseDTO generarResumen(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        // Ejecutar los 3 calculadores
        GestionInventarioMetricasDTO inventario = calculadorInventarioService.calcularPulso(inicio, fin, sucursalId);
        VentasClientesMetricasDTO ventas = calculadorVentasService.calcularMotor(inicio, fin, sucursalId);
        EficienciaOperativaMetricasDTO operativas = calculadorOperativoService.calcularSalud(inicio, fin, sucursalId);

        // Construir el prompt para Gemini
        StringBuilder prompt = new StringBuilder();
        prompt.append("Rol: \"Actúa como un Analista Financiero y Director de Operaciones (COO) experto en retail farmacéutico. A continuación, te presento los KPIs críticos del negocio del ")
              .append(inicio).append(" al ").append(fin).append(" para la sucursal ").append(sucursalId).append(":\"\n\n");

        prompt.append("Sección 1 (El Pulso - Inventario):\n")
              .append("- Rotación (IRI): ").append(inventario.getRotacionInventarioIri()).append("\n")
              .append("- GMROI: ").append(inventario.getGmroi()).append("\n")
              .append("- Sell-Through Rate (%): ").append(inventario.getSellThroughRate()).append("\n")
              .append("- Semanas de Cobertura (WOS): ").append(inventario.getWeeksOfSupplyWos()).append("\n\n");

        prompt.append("Sección 2 (El Motor - Ventas y Clientes):\n")
              .append("- Ticket Promedio: ").append(ventas.getTicketPromedio()).append("\n")
              .append("- Artículos por Ticket (UPT): ").append(ventas.getUnitsPerTransactionUpt()).append("\n")
              .append("- Tasa de Conversión (%): ").append(ventas.getTasaConversion()).append("\n")
              .append("- Margen de Utilidad Bruta (%): ").append(ventas.getMargenUtilidadBruta()).append("\n\n");

        prompt.append("Sección 3 (La Salud - Eficiencia):\n")
              .append("- Ventas por Metro Cuadrado: ").append(operativas.getVentasPorMetroCuadrado()).append("\n")
              .append("- Merma (%): ").append(operativas.getPorcentajeMerma()).append("\n")
              .append("- Punto de Equilibrio: ").append(operativas.getPuntoEquilibrio()).append("\n\n");

        prompt.append("Instrucción de Salida (Estricta): \"Analiza las correlaciones entre estas métricas (ej. cómo el tráfico y la conversión afectan el WOS y el punto de equilibrio). ")
              .append("Tu respuesta debe ser altamente escaneable y utilizar obligatoriamente esta estructura: ")
              .append("1. Un breve párrafo introductorio de diagnóstico. ")
              .append("2. Un subtítulo formateado estrictamente en negrilla y subrayado como <u><b>Hallazgos Clave</b></u> seguido de 3 viñetas con los datos combinados más impactantes o preocupantes. ")
              .append("3. Un subtítulo formateado estrictamente en negrilla y subrayado como <u><b>Recomendación Comercial</b></u> seguido de una única acción estratégica de alto impacto para corregir o potenciar los hallazgos. ")
              .append("No uses texto de relleno ni repitas las fórmulas.\"");

        // Llamada a la IA
        String resumenTexto = geminiClientService.generateContentSync(prompt.toString());

        // Ensamblar respuesta DTO final con Data Pura + Análisis
        return ResumenInteligenteResponseDTO.builder()
                .resumenGenerado(resumenTexto)
                .metricasInventario(inventario)
                .metricasVentas(ventas)
                .metricasOperativas(operativas)
                .build();
    }
}
