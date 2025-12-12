package com.farmacia.ms_transacciones.controller;

import com.farmacia.ms_transacciones.entity.Cliente;
import com.farmacia.ms_transacciones.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Define esta clase como un controlador REST
@RequestMapping("/api/transacciones/clientes") // Prefijo de la URL base
public class ClienteController {

    @Autowired // Inyecta el servicio para acceder a la lógica de negocio
    private ClienteService clienteService;

    // GET /api/transacciones/clientes?q=juan
    @GetMapping
    public ResponseEntity<List<Cliente>> listarOBuscarClientes(@RequestParam(required = false) String q) {
        // Si el parámetro 'q' (query) está presente, busca. Si no, lista todos.
        if (q != null && !q.isEmpty()) {
            return ResponseEntity.ok(clienteService.buscarClientes(q));
        }
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    // GET /api/transacciones/clientes/1
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        Cliente cliente = clienteService.obtenerClientePorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

    // POST /api/transacciones/clientes
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.crearCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    // DELETE, PUT y otras operaciones de CRUD se implementarían de forma similar...
}