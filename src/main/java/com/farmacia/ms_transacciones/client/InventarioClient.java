package com.farmacia.ms_transacciones.client;

import com.farmacia.ms_transacciones.config.UserContext;
import com.farmacia.ms_transacciones.dto.ProductoInventarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InventarioClient {

    @Autowired private RestTemplate restTemplate;

    @Value("${microservices.inventario.url}")
    private String inventarioUrl;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (UserContext.getUserId() != null) {
            headers.set("X-User-Id", String.valueOf(UserContext.getUserId()));
            headers.set("X-Username", UserContext.getUsername());
            headers.set("X-User-Role", UserContext.getUserRole());
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public ProductoInventarioDTO obtenerProducto(Integer id) {
        try {
            String url = inventarioUrl + "/productos/" + id;
            HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<ProductoInventarioDTO> res = restTemplate.exchange(url, HttpMethod.GET, entity, ProductoInventarioDTO.class);
            return res.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error conectando a Inventario para producto ID: " + id);
        }
    }

    public void registrarSalida(Integer productoId, Integer cantidad) {
        try {
            String url = inventarioUrl + "/salida";
            String jsonBody = String.format("{\"productoId\": %d, \"cantidad\": %d, \"motivo\": \"VENTA\"}", productoId, cantidad);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders());
            ResponseEntity<Void> res = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            if (!res.getStatusCode().is2xxSuccessful()) throw new RuntimeException("Error en salida inventario");
        } catch (Exception e) {
            throw new RuntimeException("Error registrando salida: " + e.getMessage());
        }
    }

    // 3. Registrar Entrada (Devolución de Stock)
    public void registrarEntrada(Integer productoId, Integer cantidad) {
        try {
            // Ajusta la URL según lo que defina tu compañero
            String url = inventarioUrl + "/entrada";

            String jsonBody = String.format("{\"productoId\": %d, \"cantidad\": %d, \"motivo\": \"DEVOLUCION\"}", productoId, cantidad);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders());

            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Void.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Inventario rechazó la devolución del producto " + productoId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al devolver stock a inventario: " + e.getMessage());
        }
    }
}
