package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.entity.Cliente;
import com.farmacia.ms_transacciones.repository.ClienteRepository;
import com.farmacia.ms_transacciones.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Le dice a Spring que esta clase es un Servicio
public class ClienteServiceImpl implements ClienteService {

    @Autowired // Inyecta el repositorio que creamos
    private ClienteRepository clienteRepository;

    @Override
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public List<Cliente> buscarClientes(String query) {
        // Ahora llamamos al método corto y limpio
        return clienteRepository.buscarPorCriterio(query);
    }
}