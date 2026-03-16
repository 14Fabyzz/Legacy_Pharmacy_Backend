package com.legacy.pharmacy.reportes.client;

import com.legacy.pharmacy.reportes.dto.internal.InventarioRawDTO;
import com.legacy.pharmacy.reportes.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Service
public class InventarioClient {

    private static final Logger log = LoggerFactory.getLogger(InventarioClient.class);
    private final RestClient restClient;

    public InventarioClient(RestClient.Builder restClientBuilder, 
                            @Value("${inventory.service.url:http://localhost:8081}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public InventarioRawDTO obtenerDatosCrudosInventario(LocalDate inicio, LocalDate fin, Integer sucursalId) {
        log.info("Solicitando datos crudos de inventario del {} al {}, sucursal {}", inicio, fin, sucursalId);
        
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/inventario/internal/datos-crudos")
                            .queryParam("inicio", inicio)
                            .queryParam("fin", fin)
                            .queryParam("sucursalId", sucursalId)
                            .build())
                    .header("X-User-Id", com.legacy.pharmacy.reportes.config.UserContext.getUserId() != null ? String.valueOf(com.legacy.pharmacy.reportes.config.UserContext.getUserId()) : "1")
                    .header("X-Username", com.legacy.pharmacy.reportes.config.UserContext.getUsername() != null ? com.legacy.pharmacy.reportes.config.UserContext.getUsername() : "System")
                    .header("X-User-Role", com.legacy.pharmacy.reportes.config.UserContext.getUserRole() != null ? com.legacy.pharmacy.reportes.config.UserContext.getUserRole() : "ADMIN")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("Error al consultar Inventory-Service: status {}", response.getStatusCode());
                        throw new ExternalServiceException("Inventory-Service no está respondiendo correctamente (Status: " + response.getStatusCode() + ")");
                    })
                    .body(InventarioRawDTO.class);
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("Fallo de conexión crítico con Inventory-Service", e);
            throw new ExternalServiceException("El servicio de inventario no está disponible para el cálculo de métricas", e);
        }
    }
}
