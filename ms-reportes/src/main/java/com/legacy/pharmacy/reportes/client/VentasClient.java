package com.legacy.pharmacy.reportes.client;

import com.legacy.pharmacy.reportes.dto.internal.VentasRawDTO;
import com.legacy.pharmacy.reportes.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Service
public class VentasClient {

    private static final Logger log = LoggerFactory.getLogger(VentasClient.class);
    private final RestClient restClient;

    public VentasClient(RestClient.Builder restClientBuilder, 
                        @Value("${ms.ventas.url:http://localhost:8081}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public VentasRawDTO obtenerDatosCrudosVentas(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        log.info("Solicitando datos crudos de ventas del {} al {}, sucursal {}", inicio, fin, sucursalId);
        
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/ventas/internal/datos-crudos")
                            .queryParam("inicio", inicio)
                            .queryParam("fin", fin)
                            .queryParam("sucursalId", sucursalId)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("Error al consultar MS-Ventas: status {}", response.getStatusCode());
                        throw new ExternalServiceException("MS-Ventas no está respondiendo correctamente (Status: " + response.getStatusCode() + ")");
                    })
                    .body(VentasRawDTO.class);
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("Fallo de conexión crítico con MS-Ventas", e);
            throw new ExternalServiceException("El servicio de ventas no está disponible para el cálculo de métricas", e);
        }
    }
}
