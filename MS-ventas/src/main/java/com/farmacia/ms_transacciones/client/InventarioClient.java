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

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservices.inventario.url}")
    private String inventarioBaseUrl;

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
            String url = inventarioBaseUrl + "/productos/" + id + "/stock";
            HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<ProductoInventarioDTO> res = restTemplate.exchange(url, HttpMethod.GET, entity,
                    ProductoInventarioDTO.class);
            return res.getBody();
        } catch (Exception e) {
            System.err.println("Error en obtenerProducto: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error conectando a Inventario para producto ID: " + id);
        }
    }

    // --- MÉTODO ACTUALIZADO: Recibe sucursalId, TipoVenta y documentoRef ---
    public void registrarSalida(Integer productoId, Integer cantidad, Integer sucursalId,
            com.farmacia.ms_transacciones.enums.TipoVenta tipoVenta, String documentoRef) {
        try {

            String url = inventarioBaseUrl + "/productos/" + productoId + "/descontar";

            // Convertir TipoVenta y documentoRef a JSON
            String tipoVentaJson = (tipoVenta != null) ? "\"" + tipoVenta.name() + "\"" : "null";
            String docRefJson = (documentoRef != null) ? "\"" + documentoRef + "\"" : "null";

            // Enviamos el TipoVenta y documentoRef en el JSON
            String jsonBody = String.format(
                    "{\"cantidad\": %d, \"motivo\": \"VENTA_SUCURSAL_%d\", \"sucursalId\": %d, \"tipoVenta\": %s, \"documentoRef\": %s}",
                    cantidad, sucursalId, sucursalId, tipoVentaJson, docRefJson);

            System.out.println("VENTA-CLIENTE: Enviando POST a Inventario: URL=" + url + ", Body=" + jsonBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders());
            ResponseEntity<Void> res = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

            if (!res.getStatusCode().is2xxSuccessful())
                throw new RuntimeException("Error: Stock insuficiente");
        } catch (Exception e) {
            throw new RuntimeException("Error registrando salida: " + e.getMessage());
        }
    }

    // --- MÉTODO RENOMBRADO: registrarDevolucion ---
    public void registrarDevolucion(Integer productoId, Integer cantidad,
            com.farmacia.ms_transacciones.enums.TipoVenta tipoVenta, String destinoProducto, String documentoRef) {
        try {
            // NOTA: La ruta cambió a .../productos/{id}/devolver
            String url = inventarioBaseUrl + "/productos/" + productoId + "/devolver";
            String tipoVentaJson = (tipoVenta != null) ? "\"" + tipoVenta.name() + "\"" : "null";
            String destinoProductoJson = (destinoProducto != null) ? "\"" + destinoProducto + "\"" : "null";
            String docRefJson = (documentoRef != null) ? "\"" + documentoRef + "\"" : "null";

            String jsonBody = String.format(
                    "{\"cantidad\": %d, \"motivo\": \"DEVOLUCION_CLIENTE\", \"tipoVenta\": %s, \"destinoProducto\": %s, \"documentoRef\": %s}",
                    cantidad, tipoVentaJson, destinoProductoJson, docRefJson);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders());
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Inventario rechazó la devolución");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al devolver stock: " + e.getMessage());
        }
    }

    // --- NUEVO MÉTODO BATCH: registrarDevolucionBatch ---
    public void registrarDevolucionBatch(com.farmacia.ms_transacciones.dto.BatchDevolucionRequestDTO requestBody) {
        try {
            System.out.println("INVENTARIO_CLIENT: Enviando Batch de Devoluciones. DocRef: "
                    + requestBody.getDocumentoRef() + " Items: " + requestBody.getItems().size());
            String url = inventarioBaseUrl + "/productos/devolver/batch";

            HttpEntity<com.farmacia.ms_transacciones.dto.BatchDevolucionRequestDTO> entity = new HttpEntity<>(
                    requestBody, getHeaders());
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("El Inventario rechazó la devolución en lote (batch)");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al devolver stock al Inventario (Batch): " + e.getMessage());
        }
    }
}