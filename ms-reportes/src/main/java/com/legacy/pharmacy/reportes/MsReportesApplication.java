package com.legacy.pharmacy.reportes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.cache.annotation.EnableCaching
public class MsReportesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReportesApplication.class, args);
    }

}
