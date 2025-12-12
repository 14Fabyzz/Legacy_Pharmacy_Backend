package com.farmacia.ms_transacciones.feign;

import com.farmacia.ms_transacciones.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

// name = nombre del servicio en Eureka (si usas)
// url = dirección directa para pruebas locales
@FeignClient(name = "ms-productos", url = "${ms.productos.url:http://localhost:8081}")
public interface InventarioClient {

    // HU-10: Obtener datos del producto (Nombre, Precio, Lote)
    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable("id") Integer id);

    // HU-10: Validar si hay suficiente stock antes de vender
    @PostMapping("/api/inventario/validar-stock")
    List<ProductoDTO> validarYReservarStock(@RequestBody List<Integer> productoIds);
}
