package com.legacy.pharmacy.inventario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testdb")
public class TestDbController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/update-view")
    public String updateView() {
        try {
            Resource resource = new ClassPathResource("schema-view.sql");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String sql = FileCopyUtils.copyToString(reader);

            long start = System.currentTimeMillis();
            jdbcTemplate.execute(sql);
            long end = System.currentTimeMillis();

            return "SUCCESS. View updated in " + (end - start) + "ms.";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    @GetMapping("/query-view")
    public List<Map<String, Object>> queryView() {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM v_stock_productos LIMIT 10");
        long end = System.currentTimeMillis();
        System.out.println("Query took: " + (end - start) + "ms");
        return result;
    }

    @GetMapping("/processes")
    public List<Map<String, Object>> getProcesses() {
        return jdbcTemplate.queryForList("SHOW FULL PROCESSLIST");
    }

    @GetMapping("/kill")
    public String killProcess(@org.springframework.web.bind.annotation.RequestParam("id") Long id) {
        jdbcTemplate.execute("KILL " + id);
        return "Killed process " + id;
    }
}
