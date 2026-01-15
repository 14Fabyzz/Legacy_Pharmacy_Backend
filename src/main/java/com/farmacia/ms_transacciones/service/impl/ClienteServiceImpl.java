package com.farmacia.ms_transacciones.service.impl;

import com.farmacia.ms_transacciones.model.Cliente;
import com.farmacia.ms_transacciones.repository.ClienteRepository;
import com.farmacia.ms_transacciones.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Cliente crearCliente(Cliente cliente) {
        // Validar si ya existe
        if(clienteRepository.findByNumeroIdentificacion(cliente.getNumeroIdentificacion()).isPresent()){
            throw new RuntimeException("El cliente ya existe con esa identificación.");
        }
        // Asignar fechas si tienes auditoría, o guardar directo
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente buscarPorIdentificacion(String identificacion) {
        return clienteRepository.findByNumeroIdentificacion(identificacion)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        // Necesitarás crear este método en el Repository
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
