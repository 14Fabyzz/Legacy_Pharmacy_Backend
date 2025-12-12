package com.farmacia.ms_transacciones.service;

import com.farmacia.ms_transacciones.entity.Cliente;
import java.util.List;

public interface ClienteService {
    Cliente crearCliente(Cliente cliente);
    Cliente obtenerClientePorId(Long id);
    List<Cliente> listarClientes();
    // Para el modal de búsqueda
    List<Cliente> buscarClientes(String query);
}
