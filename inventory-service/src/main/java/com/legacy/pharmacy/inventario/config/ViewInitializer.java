package com.legacy.pharmacy.inventario.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Inicializador de Vistas de Base de Datos.
 * 
 * Este componente se ejecuta al iniciar la aplicación y se encarga de
 * recrear la vista 'v_stock_productos'. Esto es necesario porque:
 * 1. Hibernate no actualiza vistas automáticamente.
 * 2. data.sql está desactivado (spring.sql.init.mode=never) para evitar borrar
 * datos.
 * 3. La vista debe coincidir exactamente con la entidad ProductoCard para
 * evitar errores.
 */
@Component
public class ViewInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----------------------------------------------------------");
        System.out.println("➡️  INICIANDO ACTUALIZACIÓN DE VISTAS (ViewInitializer)   ⬅️");
        System.out.println("----------------------------------------------------------");

        try {
            // Leer el archivo schema-view.sql
            Resource resource = new ClassPathResource("schema-view.sql");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String sql = FileCopyUtils.copyToString(reader);

            // Ejecutar el SQL
            jdbcTemplate.execute(sql);

            System.out.println("✅ Vista 'v_stock_productos' actualizada correctamente.");

        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO al actualizar vistas: " + e.getMessage());
            e.printStackTrace();
            // No lanzamos la excepción para no detener el arranque, pero el dashboard
            // fallará si esto no funciona.
        }

        System.out.println("----------------------------------------------------------");
    }
}
