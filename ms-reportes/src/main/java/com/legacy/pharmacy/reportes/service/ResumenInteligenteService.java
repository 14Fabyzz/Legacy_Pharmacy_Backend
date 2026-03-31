package com.legacy.pharmacy.reportes.service;

import com.legacy.pharmacy.reportes.dto.GestionInventarioMetricasDTO;
import com.legacy.pharmacy.reportes.dto.ResumenInteligenteResponseDTO;
import com.legacy.pharmacy.reportes.dto.VentasClientesMetricasDTO;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorInventarioService;
import com.legacy.pharmacy.reportes.service.metricas.CalculadorVentasService;
import com.legacy.pharmacy.reportes.service.metricas.ReportesAnaliticosService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ResumenInteligenteService {

    private final CalculadorInventarioService calculadorInventarioService;
    private final CalculadorVentasService calculadorVentasService;
    private final ReportesAnaliticosService reportesAnaliticosService;
    private final GeminiClientService geminiClientService;

    public ResumenInteligenteService(CalculadorInventarioService calculadorInventarioService,
                                     CalculadorVentasService calculadorVentasService,
                                     ReportesAnaliticosService reportesAnaliticosService,
                                     GeminiClientService geminiClientService) {
        this.calculadorInventarioService = calculadorInventarioService;
        this.calculadorVentasService = calculadorVentasService;
        this.reportesAnaliticosService = reportesAnaliticosService;
        this.geminiClientService = geminiClientService;
    }

    public ResumenInteligenteResponseDTO generarResumen(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        // Ejecutar los 2 calculadores
        GestionInventarioMetricasDTO inventario = calculadorInventarioService.calcularPulso(inicio, fin, sucursalId);
        VentasClientesMetricasDTO ventas = calculadorVentasService.calcularMotor(inicio, fin, sucursalId);

        // Obtener el Top 5 de Productos como contexto avanzado para la matriz de decisiones IA
        List<Map<String, Object>> topProductosData = reportesAnaliticosService.getTop10Productos(inicio, fin, sucursalId, null, null);
        List<Map<String, Object>> top5 = topProductosData.size() > 5 ? topProductosData.subList(0, 5) : topProductosData;

        // Obtener el Top 5 de Peor Desempeño (Baja Rotación) para generar alertas de inventario estancado
        List<Map<String, Object>> bajaRotacionData = reportesAnaliticosService.getProductosBajaRotacion(inicio, fin, sucursalId);
        List<Map<String, Object>> bajaRotacion5 = bajaRotacionData.size() > 5 ? bajaRotacionData.subList(0, 5) : bajaRotacionData;

        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat mf = new java.text.DecimalFormat("#,##0", symbols);

        // Construir el prompt para Gemini
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un Consultor de Negocios y Director de Operaciones (COO) altamente analítico pero con un lenguaje comercial directo y fácil de entender. ")
              .append("Tu objetivo es analizar los siguientes datos reales de una farmacia entre el ").append(inicio).append(" y el ").append(fin)
              .append(" y entregar un informe gerencial que cualquier dueño de negocio sin formación contable pueda entender a la perfección.\n\n");

        prompt.append("DATOS DEL MOTOR DE VENTAS:\n")
              .append("- Ticket Promedio: $").append(mf.format(ventas.getTicketPromedio())).append(" (Explica qué significa que cada cliente gaste en promedio esta cantidad).\n")
              .append("- Artículos por Ticket: ").append(ventas.getUnitsPerTransactionUpt()).append(" (Explica que un cliente suele llevar ").append(ventas.getUnitsPerTransactionUpt()).append(" productos por compra).\n")
              .append("- Margen de Utilidad Bruta: ").append(ventas.getMargenUtilidadBruta()).append("% (Explica que de cada 100 pesos vendidos, quedan ").append(ventas.getMargenUtilidadBruta()).append(" pesos libres aprox. tras pagar inventario).\n\n");

        prompt.append("DATOS DEL PULSO DE INVENTARIO:\n")
              .append("- Indice de Rotación: ").append(inventario.getRotacionInventarioIri()).append(" (Explica coloquialmente la velocidad con la que se vacían las repisas).\n")
              .append("- Retorno sobre Inversión (GMROI): ").append(inventario.getGmroi()).append(" (Explica de forma gráfica: 'Por cada peso que tienes congelado en medicinas, facturas X').\n")
              .append("- Tasa de Salida (%): ").append(inventario.getSellThroughRate()).append("% (Explica que despachaste el ").append(inventario.getSellThroughRate()).append("% de lo que compraste al proveedor).\n")
              .append("- Semanas de Cobertura: ").append(inventario.getWeeksOfSupplyWos()).append(" (Menciona para cuántas semanas de venta alcanza la bodega actual).\n\n");

        prompt.append("RÁNKING: PRODUCTOS MÁS VENDIDOS (TOP 5):\n");
        if (top5.isEmpty()) {
             prompt.append("No hubo ventas registradas en este periodo.\n\n");
        } else {
             for (Map<String, Object> p : top5) {
                  String pres = p.get("presentacion") != null ? p.get("presentacion").toString() : "UNIDAD";
                  prompt.append("- [").append(p.get("producto")).append(" (").append(pres).append(")]")
                        .append(" | Unidades: ").append(p.get("unidades"))
                        .append(" | Ingresos COP: $").append(mf.format(p.get("ventas"))).append("\n");
             }
             prompt.append("\n");
        }

        prompt.append("ALERTA: PRODUCTOS DE BAJA ROTACIÓN (TOP 5 PEOR DESEMPEÑO):\n");
        if (bajaRotacion5.isEmpty()) {
             prompt.append("No se identificaron productos con ventas críticas o nulas en este periodo.\n\n");
        } else {
             for (Map<String, Object> p : bajaRotacion5) {
                  prompt.append("- [").append(p.get("producto")).append("]")
                        .append(" | Unidades Vendidas: ").append(p.get("unidades"))
                        .append(" | Ingresos COP: $").append(mf.format(p.get("ventas"))).append("\n");
             }
             prompt.append("\n");
        }

            prompt.append("ROL Y TONO (CRÍTICO):\n")
            .append("Actúa como Consultor de Negocios y COO (Chief Operating Officer) experto en retail farmacéutico para 'Regen Salud POS'.\n")
            .append("Tu tono debe ser profesional, estratégico y directo, hablando de tú a tú al dueño de la farmacia. No seas descriptivo, sé analítico.\n\n")
            
            .append("INSTRUCCIONES DE FORMATO HTML (SIN MARKDOWN):\n")
            .append("1. Tu respuesta DEBE ser únicamente código HTML limpio listo para [innerHTML]. Prohibido usar símbolos markdown como '*' o '#'.\n")
            .append("2. Usa etiquetas semánticas: <h2> para el título principal, <h3> para secciones, <ul>/<li> para métricas y <table class='table table-sm table-modern'> para el ranking.\n\n")
            
            .append("ESTRUCTURA OBLIGATORIA DEL INFORME:\n")
            .append("1. ENCABEZADO: Genera un <h2 class='text-center'>Reporte Analítico de Negocio</h2> seguido de un <p class='text-center text-muted'>Generado por Regen Salud POS AI - [Fecha]</p>.\n")
            
            .append("2. DIAGNÓSTICO INICIAL: Un párrafo <p> que comience con un saludo (ej. '¡Hola! Como tu Consultor...') y dé un diagnóstico general usando analogías (ej. 'almacén lleno, pasillos con poco movimiento') traduciendo los números a la realidad del capital de trabajo.\n")
            
            .append("3. SECCIÓN: <h3>El Motor de tus Ventas: ¿Cómo está funcionando la caja?</h3>\n")
            .append("   - Lista <ul> con viñetas para: Ticket Promedio (valor de cada visita), Artículos por Ticket (comportamiento del carrito) y Margen de Utilidad Bruta (dinero base para cubrir gastos).\n")
            
            .append("4. SECCIÓN: <h3>El Pulso de tu Inventario: ¿Qué tan eficiente eres manejando tus productos?</h3>\n")
            .append("   - Lista <ul> para: Índice de Rotación (velocidad de vaciado), Retorno sobre Inversión (dinero generado por cada peso 'congelado' en bodega) y Semanas de Cobertura (WOS).\n")
            .append("   - REGLA DE TRADUCCIÓN: Si las 'Semanas de Cobertura' son excesivas, indica cuántos AÑOS de stock representan y marca una alerta roja sobre el capital inmovilizado.\n")
            
            .append("5. SECCIÓN: <h3>Ranking: Productos Más Vendidos (TOP 5)</h3>\n")
            .append("   - Construye una <table class='table table-sm table-modern'> con columnas: <b>Producto</b>, <b>Unidades Vendidas</b> e <b>Ingresos (COP)</b> utilizando los datos inyectados.\n\n")
            
            .append("6. SECCIÓN: <h3>Alerta: Productos de Baja Rotación (Peor Desempeño)</h3>\n")
            .append("   - Construye una <table class='table table-sm table-modern'> mostrando los productos del Top 5 de baja rotación. Usa un tono de alerta para advertir sobre el espacio inmovilizado en la repisa.\n\n")
            
            .append("7. CIERRE: <h3>Recomendación Comercial Clave</h3>\n")
            .append("   - Un párrafo final con una única estrategia agresiva y accionable (ej. campañas de liquidación para liberar capital enterrado).\n\n")
            
            .append("REGLA DE ORO: PROHIBIDO usar siglas como 'GMROI', 'WOS' o 'IRI' sin explicar primero qué significan para el farmaceuta en términos de dinero y tiempo.");
       
        // Llamada a la IA
        String resumenTexto = geminiClientService.generateContentSync(prompt.toString());

        // Ensamblar respuesta DTO final con Data Pura + Análisis
        return ResumenInteligenteResponseDTO.builder()
                .resumenGenerado(resumenTexto)
                .metricasInventario(inventario)
                .metricasVentas(ventas)
                .build();
    }
}
