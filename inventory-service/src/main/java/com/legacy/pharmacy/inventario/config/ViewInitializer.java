package com.legacy.pharmacy.inventario.config;

/**
 * ============================================================
 * ⚠️ CLASE DESHABILITADA — ANTIPATRÓN CRÍTICO RESUELTO
 * ============================================================
 *
 * PROBLEMA ORIGINAL:
 * Esta clase implementaba CommandLineRunner y ejecutaba un DDL
 * (CREATE OR REPLACE VIEW v_stock_productos) en cada arranque
 * de la aplicación mediante jdbcTemplate.execute(sql).
 *
 * IMPACTO EN PRODUCCIÓN:
 * El DDL de la vista adquiere un "Metadata Lock" exclusivo en
 * MySQL sobre las tablas base (productos, lotes, categorías, etc.).
 * Cualquier query DML (SELECT) en ejecución concurrente queda
 * bloqueada en estado "Waiting for table metadata lock", saturando
 * el pool de conexiones y congelando el Dashboard del Frontend.
 *
 * SOLUCIÓN APLICADA:
 * La vista 'v_stock_productos' NUNCA debe recrearse en tiempo de
 * ejecución (runtime). Debe existir únicamente como un objeto
 * estático en la base de datos, creado UNA SOLA VEZ mediante el
 * script: src/main/resources/schema-view.sql
 *
 * Para aplicar o actualizar la vista, ejecutar manualmente el
 * script en un cliente MySQL (Workbench, DBeaver) durante una
 * ventana de mantenimiento con bajo tráfico.
 *
 * Esta clase puede eliminarse de forma segura. Queda como
 * documentación del antipatrón para evitar su reintroducción.
 * ============================================================
 */
public class ViewInitializer {
    // Clase intencionalmente vacía.
    // NO agregar @Component, @Service ni ninguna anotación de Spring.
    // VER COMENTARIO JAVADOC SUPERIOR.
}
